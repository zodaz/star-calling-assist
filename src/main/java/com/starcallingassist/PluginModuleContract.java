package com.starcallingassist;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;

public class PluginModuleContract
{
	@Setter
	protected Injector injector;

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

	public void onSecondElapsed(int secondsSinceStartup)
	{
		//
	}

	public void dispatch(Object event)
	{
		eventBus.post(event);
	}
}
