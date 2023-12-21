package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.objects.StarLocation;
import java.util.List;

public interface MasterPanelDecorator
{
	void onWorldHopRequest(WorldHopRequest worldHopRequest);

	void onPanelActivated();

	void onPanelDeactivated();

	List<StarLocation> getCurrentPlayerRegions();
}
