package com.starcallingassist.modules.crowdsourcing;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.AnnouncementReceived;
import com.starcallingassist.events.AnnouncementRefreshFailed;
import com.starcallingassist.events.AnnouncementsRefreshed;
import com.starcallingassist.events.NavButtonClicked;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import com.starcallingassist.objects.Star;
import com.starcallingassist.services.HttpService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnouncementModule extends PluginModuleContract
{
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private Client client;

	@Inject
	private Gson gson;

	@Inject
	private HttpService httpService;

	private static final int[] starMiningAnimationIDs = {
		AnimationID.MINING_CRASHEDSTAR_BRONZE,
		AnimationID.MINING_CRASHEDSTAR_IRON,
		AnimationID.MINING_CRASHEDSTAR_STEEL,
		AnimationID.MINING_CRASHEDSTAR_BLACK,
		AnimationID.MINING_CRASHEDSTAR_MITHRIL,
		AnimationID.MINING_CRASHEDSTAR_ADAMANT,
		AnimationID.MINING_CRASHEDSTAR_RUNE,
		AnimationID.MINING_CRASHEDSTAR_GILDED,
		AnimationID.MINING_CRASHEDSTAR_DRAGON,
		AnimationID.MINING_CRASHEDSTAR_DRAGON_UPGRADED,
		AnimationID.MINING_CRASHEDSTAR_DRAGON_OR,
		AnimationID.MINING_CRASHEDSTAR_DRAGON_OR_TRAILBLAZER,
		AnimationID.MINING_CRASHEDSTAR_INFERNAL,
		AnimationID.MINING_CRASHEDSTAR_3A,
		AnimationID.MINING_CRASHEDSTAR_CRYSTAL
	};

	private final HashMap<Integer, AnnouncedStar> stars = new HashMap<>();

	private Long announcementsLastRefreshedAt = null;

	private boolean isRefreshing = false;

	private long lastInteractionTimestamp = System.currentTimeMillis();

	private boolean sidePanelOpened = false;

	@Override
	public void startUp()
	{
		autoRefresh(0);
	}

	@Override
	public void onSecondElapsed(int secondsSinceStartup)
	{
		autoRefresh(secondsSinceStartup);
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("endpoint") || event.getKey().equals("authorization"))
		{
			announcementsLastRefreshedAt = null;
			this.refreshAnnouncements();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.LOGGED_IN)
		{
			refreshAnnouncements();
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (client.getLocalPlayer() != event.getActor())
		{
			return;
		}

		int animId = event.getActor().getAnimation();
		if (Arrays.stream(starMiningAnimationIDs).noneMatch(i -> i == animId))
		{
			return;
		}

		lastInteractionTimestamp = System.currentTimeMillis();
	}

	@Subscribe
	public void onNavButtonClicked(NavButtonClicked event)
	{
		//sidePanelOpened = event.getButton().isSelected();

		if (sidePanelOpened)
		{
			SwingUtilities.invokeLater(this::refreshAnnouncements);
		}
	}

	private void autoRefresh(int seconds)
	{
		// We'll want to refresh the star list every three minutes when the side panel is closed.
		// This allows us to auto-call "outdated" or unconfirmed stars when not participating.
		if (!sidePanelOpened && config.autoCall() && seconds % (60 * 3) == 0)
		{
			refreshAnnouncements();
			return;
		}

		// When the side-panel is open, we'll want to refresh every thirty seconds.
		if (sidePanelOpened && seconds % 30 == 0)
		{
			refreshAnnouncements();
			return;
		}

		// Otherwise, we'll still want to refresh every thirty seconds if we've recently interacted with a star.
		boolean recentlyInteracted = (int) ((System.currentTimeMillis() - lastInteractionTimestamp) / 1000) < (60 * 2);
		if (recentlyInteracted && seconds % 30 == 0)
		{
			refreshAnnouncements();
		}
	}

	private void refreshAnnouncements()
	{
		if (isRefreshing || config.getAuthorization().isEmpty() || config.getEndpoint().isEmpty())
		{
			return;
		}

		if (announcementsLastRefreshedAt != null && (int) ((System.currentTimeMillis() - announcementsLastRefreshedAt) / 1000) < 20)
		{
			return;
		}

		isRefreshing = true;

		try
		{
			httpService.get(new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					isRefreshing = false;
					dispatch(new AnnouncementRefreshFailed(e.getMessage()));
					call.cancel();
				}

				@Override
				public void onResponse(Call call, Response res) throws IOException
				{
					if (!res.isSuccessful())
					{
						String message = res.message();
						res.close();
						isRefreshing = false;
						dispatch(new AnnouncementRefreshFailed(message));
						return;
					}

					ResponseBody body = res.body();
					if (body == null)
					{
						res.close();
						isRefreshing = false;
						dispatch(new AnnouncementRefreshFailed("No response received."));
						return;
					}

					List<Integer> outdatedWorlds = new ArrayList<>(stars.keySet());

					try
					{
						JsonArray array = gson.fromJson(body.string(), JsonArray.class);
						if (array.size() < 1)
						{
							res.close();
							isRefreshing = false;
							dispatch(new AnnouncementsRefreshed(new ArrayList<>(stars.values())));
							return;
						}

						for (final JsonElement element : array)
						{
							AnnouncedStar announcedStar = AnnouncedStar.fromJsonObject(element.getAsJsonObject());
							if (announcedStar == null)
							{
								continue;
							}

							Integer world = announcedStar.getStar().getWorld();
							outdatedWorlds.remove(world);

							AnnouncedStar existingAnnouncement = stars.get(world);
							if (existingAnnouncement != null && !announcedStar.isSuccessorTo(existingAnnouncement))
							{
								continue;
							}

							stars.put(world, announcedStar);
							dispatch(new AnnouncementReceived(announcedStar));
						}
					}
					catch (Exception e)
					{
						res.close();
						isRefreshing = false;
						dispatch(new AnnouncementRefreshFailed(e.getMessage()));
						return;
					}

					outdatedWorlds.forEach(world -> {
						AnnouncedStar outdated = stars.remove(world);
						AnnouncedStar deadStarAnnouncement = new AnnouncedStar(
							new Star(
								outdated.getStar().getWorld(),
								outdated.getStar().getLocation(),
								null
							),
							System.currentTimeMillis() / 1000L,
							outdated.getPlayerName()
						);

						dispatch(new AnnouncementReceived(deadStarAnnouncement));
					});

					announcementsLastRefreshedAt = System.currentTimeMillis();
					dispatch(new AnnouncementsRefreshed(new ArrayList<>(stars.values())));
					res.close();
					isRefreshing = false;
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			isRefreshing = false;
			dispatch(new AnnouncementRefreshFailed(e.getMessage()));
		}
	}
}
