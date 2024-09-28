package com.starcallingassist.events;

import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AnnouncementAttributesUpdated
{
	@Getter
	ConcurrentHashMap<Integer, StarListEntryAttributes> announcementAttributes;
}
