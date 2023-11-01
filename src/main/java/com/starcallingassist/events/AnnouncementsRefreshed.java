package com.starcallingassist.events;

import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AnnouncementsRefreshed
{
	@Getter
	private List<AnnouncedStar> announcements;
}
