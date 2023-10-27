package com.starcallingassist;

import com.google.inject.Injector;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;

public class StarModuleContract
{
	@Setter
	protected StarCallingAssistConfig config;

	@Setter
	protected Injector injector;

	@Setter
	protected StarCallingAssistPlugin plugin;

	@Inject
	private EventBus eventBus;

	public void startUp()
	{
		//
	}

	public void shutDown()
	{
		//
	}

	public void dispatch(Object event)
	{
		eventBus.post(event);
	}
}
