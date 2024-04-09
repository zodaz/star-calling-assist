package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.enums.Region;
import com.starcallingassist.events.ShowWorldPointOnWorldMapRequested;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
import com.starcallingassist.objects.StarLocation;
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

	List<StarLocation> getCurrentPlayerLocations();

	int getCurrentWorldId();

	void onWorldHopRequest(WorldHopRequest request);

	void onShowWorldPointOnWorldMapRequested(ShowWorldPointOnWorldMapRequested showWorldPointOnWorldMapRequested);
}
