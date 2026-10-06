package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.enums.Region;
import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.events.ShowStarOnWorldMapRequested;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
import com.starcallingassist.enums.StarLocationDetails;
import java.util.List;

public interface StarListGroupEntryDecorator
{
	boolean hasAuthorization();

	boolean shouldEstimateTier();

	boolean showFreeToPlayWorlds();

	boolean showMembersWorlds();

	boolean showPvPWorlds();

	boolean showHighRiskWorlds();

	TotalLevelType maxTotalLevel();

	int minTier();

	int maxTier();

	int minDeadTime();

	List<Region> visibleRegions();

	Boolean showWorldTypeColumn();

	Boolean showTierColumn();

	Boolean showDeadTimeColumn();

	Boolean showFoundByColumn();

	String getLocationFilter();

	boolean isShortestPathPluginAvailable();

	List<StarLocationDetails> getCurrentPlayerLocations();

	int getCurrentWorldId();

	void onWorldHopRequest(WorldHopRequest request);

	void onShowWorldPointOnWorldMapRequested(ShowStarOnWorldMapRequested showStarOnWorldMapRequested);

	void onRouteViaShortestPathRequested(RouteViaShortestPathRequested routeViaShortestPathRequested);
}
