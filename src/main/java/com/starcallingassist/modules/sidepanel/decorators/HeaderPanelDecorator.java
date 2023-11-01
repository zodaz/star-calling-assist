package com.starcallingassist.modules.sidepanel.decorators;

import com.starcallingassist.modules.sidepanel.enums.OrderBy;

public interface HeaderPanelDecorator
{
	boolean hasAuthorization();

	void onSortingChanged(OrderBy orderBy);
}
