package com.starcallingassist.events;

import javax.annotation.Nullable;
import lombok.Data;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.events.ConfigChanged;

@Data
public class StarCallingAssistConfigChanged
{
	/**
	 * The parent group for the key.
	 * <p>
	 * Typically set to the name of a plugin to prevent potential collisions
	 * between other key values that may have the same name.
	 */
	private String group;

	/**
	 * The profile that has changed, if any
	 *
	 * @see RuneScapeProfile#getKey()
	 */
	@Nullable
	private String profile;
	/**
	 * The configuration key that has been modified.
	 */
	private String key;
	/**
	 * The previous value of the entry.
	 */
	private String oldValue;
	/**
	 * The new value of the entry, null if the entry has been unset.
	 */
	private String newValue;

	public static StarCallingAssistConfigChanged fromConfigChangedEvent(ConfigChanged event)
	{
		StarCallingAssistConfigChanged pluginConfigChanged = new StarCallingAssistConfigChanged();

		pluginConfigChanged.setGroup(event.getGroup());
		pluginConfigChanged.setProfile(event.getProfile());
		pluginConfigChanged.setKey(event.getKey());
		pluginConfigChanged.setOldValue(event.getOldValue());
		pluginConfigChanged.setNewValue(event.getNewValue());

		return pluginConfigChanged;
	}
}
