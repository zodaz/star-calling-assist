package com.starcallingassist.modules.starobserver;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class StarObserverModule extends StarModuleContract
{
	@Inject
	private Client client;

	public static final int[] STAR_TIER_IDS = new int[]{
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

	protected Star trackedStar = null;

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
			dispatch(new StarDiscovered(nearbyStar));
			return;
		}

		if (!Objects.equals(nearbyStar.getTier(), trackedStar.getTier()))
		{
			nearbyStar.setCurrentMiners(trackedStar.getCurrentMiners());

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

		Star nearbyStar = new Star(client.getWorld(), despawnedGameObject.getWorldLocation());
		if (!nearbyStar.isSameAs(trackedStar))
		{
			return;
		}

		// When a spawn tier is exhausted, it despawns, and a star of the next tier spawns in its place.
		// Only when the despawned star was a tier 1, we can consider this a depleted star.
		if (trackedStar.getTier() > 1)
		{
			return;
		}

		trackedStar = nearbyStar;
		dispatch(new StarDepleted(nearbyStar));
	}

	protected void onGameTick(GameTick event)
	{
		if (trackedStar == null || trackedStar.getTier() == null)
		{
			return;
		}

		if (!isStarNearby(trackedStar))
		{
			dispatch(new StarAbandoned(trackedStar));
			trackedStar = null;
			return;
		}

		if (!isStarWithinRenderDistance(trackedStar))
		{
			trackedStar.setCurrentMiners(null);
			return;
		}

		trackedStar.setCurrentMiners(countMiners(trackedStar));
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

	private boolean isStarNearby(@Nonnull Star star)
	{
		return star.getLocation().getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation()) <= 32;
	}

	protected Integer countMiners(@Nonnull Star star)
	{
		WorldPoint starLocation = star.getLocation().getWorldPoint();

		WorldArea areaX = new WorldArea(starLocation.dx(-1), 4, 2);
		WorldArea areaY = new WorldArea(starLocation.dy(-1), 2, 4);

		int miners = 0;

		for (Player player : client.getPlayers())
		{
			if (player.getWorldLocation().isInArea2D(areaX, areaY))
			{
				miners++;
			}
		}

		return miners;
	}
}
