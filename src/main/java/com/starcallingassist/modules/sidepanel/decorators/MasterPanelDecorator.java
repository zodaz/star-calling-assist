package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.events.ShowStarOnWorldMapRequested;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.enums.StarLocationDetails;
import java.util.List;

public interface MasterPanelDecorator
{
	void onWorldHopRequest(WorldHopRequest worldHopRequest);

	void onShowWorldPointOnWorldMapRequested(ShowStarOnWorldMapRequested showStarOnWorldMapRequested);

	void onRouteViaShortestPathRequested(RouteViaShortestPathRequested routeViaShortestPathRequested);

	List<StarLocationDetails> getCurrentPlayerRegions();

	void onSidePanelVisibilityChanged(boolean isVisible);
}
