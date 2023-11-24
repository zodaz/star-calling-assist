package com.starcallingassist.events;

import com.starcallingassist.modules.crowdsourcing.objects.CallStarPayload;
import com.starcallingassist.objects.Star;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BroadcastSuccessful
{
	@Getter
	private final Star star;

	@Getter
	private final CallStarPayload payload;
}
