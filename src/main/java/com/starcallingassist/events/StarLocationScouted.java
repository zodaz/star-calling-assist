package com.starcallingassist.events;

import com.starcallingassist.enums.StarLocationDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@AllArgsConstructor
public class StarLocationScouted
{
	@Getter
	private final StarLocationDetails location;
}
