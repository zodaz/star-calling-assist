package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NavButtonClicked
{
	private final boolean isVisible;
}
