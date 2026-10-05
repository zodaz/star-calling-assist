package com.starcallingassist.events;

import com.starcallingassist.enums.StarLocationDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event indicates that the player left the region from which a specific star location can be scouted.
 */
@AllArgsConstructor
public class StarLocationRegionExited
{
	@Getter
	private final StarLocationDetails location;
}
