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
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.AnimationID;
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
            AnimationID.HUMAN_MINING_BRONZE_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_IRON_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_STEEL_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_BLACK_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_MITHRIL_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_ADAMANT_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_RUNE_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_GILDED_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_DRAGON_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_DRAGON_PICKAXE_PRETTY_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_ZALCANO_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_TRAILBLAZER_PICKAXE_NO_INFERNAL_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_INFERNAL_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_3A_PICKAXE_NOREACHFORWARD,
            AnimationID.HUMAN_MINING_CRYSTAL_PICKAXE_NOREACHFORWARD
	};

	private final HashMap<Integer, AnnouncedStar> stars = new HashMap<>();

	private Long announcementsLastRefreshedAt = null;

	private boolean isRefreshing = false;

	private long lastInteractionTimestamp = System.currentTimeMillis();

	private boolean sidePanelShowing = false;

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
		sidePanelShowing = event.isVisible();

		if (sidePanelShowing)
		{
			SwingUtilities.invokeLater(this::refreshAnnouncements);
		}
	}

	private void autoRefresh(int seconds)
	{
		// We'll want to refresh the star list every three minutes when the side panel is closed.
		// This allows us to auto-call "outdated" or unconfirmed stars when not participating.
		if (!sidePanelShowing && config.autoCall() && seconds % (60 * 3) == 0)
		{
			refreshAnnouncements();
			return;
		}

		// When the side-panel is open, we'll want to refresh every thirty seconds.
		if (sidePanelShowing && seconds % 30 == 0)
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
							Star.fromExistingWithTierChange(outdated.getStar(), null),
							System.currentTimeMillis() / 1000L
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
