package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AnnouncementRefreshFailed
{
	@Getter
	private final String message;
}
