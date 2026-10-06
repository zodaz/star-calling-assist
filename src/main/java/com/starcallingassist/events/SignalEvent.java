package com.starcallingassist.events;

import com.starcallingassist.enums.SignalEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event used for simple signaling events that doesn't have a payload. Carries a {@link SignalEventType}.
 */
@Getter
@AllArgsConstructor
public class SignalEvent
{
	private final SignalEventType signal;
}
