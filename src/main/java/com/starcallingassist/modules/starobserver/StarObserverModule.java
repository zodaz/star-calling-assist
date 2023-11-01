package com.starcallingassist.modules.starobserver;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class StarObserverModule extends PluginModuleContract
{
	@Inject
	private Client client;

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

	private Star trackedStar = null;
	private boolean isNearStar = false;

	@Override
	public void shutDown()
	{
		trackedStar = null;
		isNearStar = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (!isValidStarObject(gameObject))
		{
			return;
		}

		Star nearbyStar = new Star(client.getWorld(), gameObject.getWorldLocation(), calculateStarTier(gameObject));
		if (trackedStar == null || !nearbyStar.isSameAs(trackedStar))
		{
			trackedStar = nearbyStar;
			isNearStar = false;
			dispatch(new StarDiscovered(nearbyStar));
			return;
		}

		if (!Objects.equals(nearbyStar.getTier(), trackedStar.getTier()))
		{
			trackedStar = nearbyStar;
			dispatch(new StarTierChanged(nearbyStar));
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject despawnedGameObject = event.getGameObject();
		if (trackedStar == null || !isValidStarObject(despawnedGameObject))
		{
			return;
		}

		Star despawnedStar = new Star(client.getWorld(), despawnedGameObject.getWorldLocation());
		if (!despawnedStar.isSameAs(trackedStar))
		{
			return;
		}

		// When the player leaves the area, the star object will automatically despawn.
		// We will ignore these objects, since we can't rely on them being accurate.
		if (!isStarWithinRenderDistance(trackedStar))
		{
			return;
		}

		// When a spawn tier is exhausted, it despawns, and a star of the next tier spawns in its place.
		// Only when the despawned star was a tier 1, we can consider this a depleted star.
		if (trackedStar.getTier() > 1)
		{
			return;
		}

		trackedStar = despawnedStar;
		isNearStar = false;
		dispatch(new StarDepleted(despawnedStar));
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (trackedStar == null || trackedStar.getTier() == null)
		{
			return;
		}

		if (trackedStar.getWorld() == client.getWorld() && isStarWithinRenderDistance(trackedStar))
		{
			if (!isNearStar)
			{
				isNearStar = true;
				dispatch(new StarApproached(trackedStar));
			}

			return;
		}

		if (isNearStar)
		{
			isNearStar = false;
			dispatch(new StarAbandoned(trackedStar));
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

	protected Boolean isValidStarObject(GameObject object)
	{
		return this.calculateStarTier(object) != null;
	}

	private boolean isStarWithinRenderDistance(@Nonnull Star star)
	{
		return star.getLocation().getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation()) <= 13;
	}
}
