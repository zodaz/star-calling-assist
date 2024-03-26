package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class NavButtonClicked
{
	private final boolean isVisible;
}
