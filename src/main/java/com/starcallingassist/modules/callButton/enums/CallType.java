package com.starcallingassist.modules.callButton.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CallType
{
	STAR(5),
	DEAD(6),
	DEAD_PRIVATE(7);

	@Getter
	private final int op;
}
