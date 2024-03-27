package com.starcallingassist.modules.starobserver;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.AnnouncementReceived;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarLocationScouted;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.events.WorldStarUpdated;
import com.starcallingassist.objects.Star;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.NullNpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

public class StarObserverModule extends PluginModuleContract
{
	private static final int STAR_NPC_ID = NullNpcID.NULL_10629;
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private static final int[] STAR_TIER_IDS = new int[]{
		ObjectID.CRASHED_STAR_41229,
		ObjectID.CRASHED_STAR_41228,
		ObjectID.CRASHED_STAR_41227,
		ObjectID.CRASHED_STAR_41226,
		ObjectID.CRASHED_STAR_41225,
		ObjectID.CRASHED_STAR_41224,
		ObjectID.CRASHED_STAR_41223,
		ObjectID.CRASHED_STAR_41021,
		ObjectID.CRASHED_STAR,
	};

	public final ConcurrentHashMap<Integer, Star> currentStars = new ConcurrentHashMap<>();

	private boolean isNearStarLocation = false;

	private NPC currentStarNpc = null;

	@Override
	public void startUp()
	{
		detectExistingStarNpc();
		clientThread.invokeLater(() -> dispatch(new WorldStarUpdated(currentStars.get(client.getWorld()))));
	}

	@Override
	public void shutDown()
	{
		isNearStarLocation = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (!isValidStarObject(gameObject))
		{
			return;
		}

		Star observedStar = new Star(client.getWorld(), gameObject.getWorldLocation(), calculateStarTier(gameObject));
		Star lastKnownStar = currentStars.get(observedStar.getWorld());

		if (lastKnownStar == null || !observedStar.isSameAs(lastKnownStar))
		{
			currentStars.put(observedStar.getWorld(), observedStar);
			dispatch(new WorldStarUpdated(observedStar));
			dispatch(new StarScouted(observedStar));
			return;
		}

		if (!Objects.equals(observedStar.getTier(), lastKnownStar.getTier()))
		{
			currentStars.put(observedStar.getWorld(), observedStar);
			dispatch(new WorldStarUpdated(observedStar));
			dispatch(new StarTierChanged(observedStar));
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject despawnedGameObject = event.getGameObject();
		if (!isValidStarObject(despawnedGameObject))
		{
			return;
		}

		Star lastKnownStar = currentStars.get(client.getWorld());
		if (lastKnownStar == null)
		{
			return;
		}

		Star despawnedStar = new Star(client.getWorld(), despawnedGameObject.getWorldLocation(), null);
		if (!despawnedStar.isSameAs(lastKnownStar))
		{
			return;
		}

		// When the player leaves the area, the star object will automatically despawn.
		// We will ignore these objects, since we can't rely on them being accurate.
		if (!isStarLocationWithinRenderDistance(lastKnownStar))
		{
			return;
		}

		// When a spawn tier is exhausted, it despawns, and a star of the next tier spawns in its place.
		// Only when players mine-down a tier-1 star, we can consider the star fully depleted.
		if (lastKnownStar.getTier() > 1)
		{
			return;
		}

		currentStars.remove(lastKnownStar.getWorld());
		dispatch(new StarDepleted(despawnedStar));
		dispatch(new WorldStarUpdated(null));
		isNearStarLocation = false;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE || !event.getMessage().equals("The star disintegrates into dust."))
		{
			return;
		}

		Star lastKnownStar = currentStars.get(client.getWorld());
		if (lastKnownStar == null)
		{
			return;
		}

		// Since for this 'despawn event' we won't have a game-object to check against, we'll want to
		// make sure we're close to the area that the current world's star was indicated to be.
		if (!isStarLocationWithinRenderDistance(lastKnownStar))
		{
			return;
		}

		// Finally, we'll want to make sure that we're only handling despawns here that are the result
		// of the star's spawn timer elapsing, and not those that are the result of mining down a T1
		// We could rely on this chat-based logic alone, but this won't get triggered when you're
		// not actively mining the star when it despawns, meaning having both checks is better.
		if (lastKnownStar.getTier() == 1)
		{
			return;
		}

		Star despawnedStar = new Star(lastKnownStar.getWorld(), lastKnownStar.getLocation(), null);
		currentStars.remove(despawnedStar.getWorld());

		dispatch(new StarDepleted(despawnedStar));
		dispatch(new WorldStarUpdated(null));
		isNearStarLocation = false;
	}

	@Subscribe
	public void onAnnouncementReceived(AnnouncementReceived event)
	{
		Star updatedStar = event.getAnnouncement().getStar();
		if (client.getWorld() == updatedStar.getWorld())
		{
			updateCurrentStarForCurrentWorld(updatedStar);
		}
		else
		{
			updateCurrentStarForOtherWorlds(updatedStar);
		}
	}

	@Subscribe
	public void onStarLocationScouted(StarLocationScouted event)
	{
		Star star = currentStars.get(client.getWorld());
		if (star == null)
		{
			return;
		}

		if (!Objects.equals(event.getLocation().getWorldPoint(), star.getLocation().getWorldPoint()))
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, star.getLocation().getWorldPoint());
		if (lp == null)
		{
			return;
		}

		GameObject[] objects = client.getScene().getTiles()[client.getPlane()][lp.getSceneX()][lp.getSceneY()].getGameObjects();
		if (objects == null)
		{
			return;
		}

		for (GameObject object : objects)
		{
			if (isValidStarObject(object))
			{
				return;
			}
		}

		dispatch(new StarMissing(new Star(star.getWorld(), star.getLocation().getWorldPoint(), null)));
		currentStars.remove(star.getWorld());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Star star = currentStars.get(client.getWorld());
		if (star == null)
		{
			return;
		}

		star.setNpc(currentStarNpc);

		if (isStarLocationWithinRenderDistance(star))
		{
			if (!isNearStarLocation)
			{
				isNearStarLocation = true;
				dispatch(new StarApproached(star));
			}

			return;
		}

		if (isNearStarLocation)
		{
			isNearStarLocation = false;
			dispatch(new StarAbandoned(star));
		}
	}

	@Subscribe
	public void onWorldChanged(WorldChanged event)
	{
		isNearStarLocation = false;
		dispatch(new WorldStarUpdated(currentStars.get(client.getWorld())));
	}

	private void updateCurrentStarForOtherWorlds(Star updatedStar)
	{
		if (updatedStar.getTier() == null)
		{
			currentStars.remove(updatedStar.getWorld());
		}
		else
		{
			currentStars.put(updatedStar.getWorld(), updatedStar);
		}
	}

	private void updateCurrentStarForCurrentWorld(Star updatedStar)
	{
		Star existingStar = currentStars.get(client.getWorld());
		if (existingStar == null && updatedStar.getTier() == null)
		{
			return;
		}

		if (existingStar == null)
		{
			currentStars.put(updatedStar.getWorld(), updatedStar);
			dispatch(new WorldStarUpdated(updatedStar));
			return;
		}

		if (!existingStar.isSameAs(updatedStar))
		{
			currentStars.put(updatedStar.getWorld(), updatedStar);
			dispatch(new WorldStarUpdated(updatedStar));
			return;
		}

		if (updatedStar.getTier() == null)
		{
			currentStars.remove(updatedStar.getWorld());
			dispatch(new WorldStarUpdated(null));
			return;
		}

		if (existingStar.getTier() > updatedStar.getTier())
		{
			currentStars.put(updatedStar.getWorld(), updatedStar);
			dispatch(new WorldStarUpdated(updatedStar));
		}
	}

	protected Integer calculateStarTier(GameObject object)
	{
		if (object == null)
		{
			return null;
		}

		for (int i = 0; i < STAR_TIER_IDS.length; i++)
		{
			if (object.getId() == STAR_TIER_IDS[i])
			{
				return i + 1;
			}
		}

		return null;
	}

	private void detectExistingStarNpc()
	{
		client.getNpcs().forEach(npc ->
		{
			if (npc.getId() == STAR_NPC_ID)
			{
				currentStarNpc = npc;
			}
		});
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == STAR_NPC_ID)
		{
			this.currentStarNpc = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().getId() == STAR_NPC_ID)
		{
			this.currentStarNpc = null;
		}
	}

	protected Boolean isValidStarObject(GameObject object)
	{
		return this.calculateStarTier(object) != null;
	}

	private boolean isStarLocationWithinRenderDistance(@Nonnull Star star)
	{
		WorldArea worldArea = star.getLocation().getWorldArea();

		if (worldArea == null)
		{
			return false;
		}

		return worldArea.distanceTo(client.getLocalPlayer().getWorldLocation()) <= 13;
	}
}
