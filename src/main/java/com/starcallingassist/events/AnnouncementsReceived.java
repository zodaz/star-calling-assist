package com.starcallingassist.events;

import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class AnnouncementsReceived
{
	@Getter
	private final List<AnnouncedStar> announcements;
}
