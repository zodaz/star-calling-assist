package com.starcallingassist.events;

import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.events.ConfigChanged;

@AllArgsConstructor
@Data
public class PluginConfigChanged
{
	/**
	 * The parent group for the key.
	 * <p>
	 * Typically set to the name of a plugin to prevent potential collisions
	 * between other key values that may have the same name.
	 */
	private final String group;

	/**
	 * The profile that has changed, if any
	 *
	 * @see RuneScapeProfile#getKey()
	 */
	@Nullable
	private final String profile;
	/**
	 * The configuration key that has been modified.
	 */
	private final String key;
	/**
	 * The previous value of the entry.
	 */
	private final String oldValue;
	/**
	 * The new value of the entry, null if the entry has been unset.
	 */
	private final String newValue;

	public static PluginConfigChanged fromRuneLiteEvent(ConfigChanged event)
	{
		return new PluginConfigChanged(
			event.getGroup(),
			event.getProfile(),
			event.getKey(),
			event.getOldValue(),
			event.getNewValue()
		);
	}
}
