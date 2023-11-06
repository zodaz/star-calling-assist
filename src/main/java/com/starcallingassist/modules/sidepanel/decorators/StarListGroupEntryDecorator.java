package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.enums.Region;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
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

	int getCurrentWorldId();

	void onWorldHopRequest(WorldHopRequest request);
}
