package com.starcallingassist.events;

import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: MAKE INTO LIST OF ANNOUNCED STARS
@AllArgsConstructor
public class AnnouncementReceived
{
	@Getter
	private final AnnouncedStar announcement;
}
