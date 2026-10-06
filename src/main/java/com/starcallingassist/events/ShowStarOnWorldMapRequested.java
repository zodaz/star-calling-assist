package com.starcallingassist.events;

import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ShowStarOnWorldMapRequested
{
	@Getter
	private final StarListEntryAttributes starListEntryAttributes;
}
