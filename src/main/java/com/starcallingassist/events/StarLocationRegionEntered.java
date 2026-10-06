package com.starcallingassist.events;

import com.starcallingassist.enums.StarLocationDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event indicates that the player entered the region from which a specific star location can be scouted.
 */
@AllArgsConstructor
public class StarLocationRegionEntered
{
	@Getter
	private final StarLocationDetails location;
}
