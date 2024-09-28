package com.starcallingassist.modules.spriteutil;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.game.SpriteManager;

/**
 * Utility module that handles loading sprites from cache.
 */
public class SpriteUtilModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private SpriteManager spriteManager;

	/**
	 * Consumer callbacks waiting to be called in order to load sprites.
	 */
	private static final Queue<Consumer<SpriteManager>> callbackQueue = new LinkedList<>();

	/**
	 * Register a one-time callback to be called when sprites are able to be loaded from cache.
	 * @param callback The callback to call when sprites are ready to be loaded.
	 */
	public static void registerLoadSpritesCallback(Consumer<SpriteManager> callback)
	{
		callbackQueue.add(callback);
	}

	@Override
	public void startUp()
	{
		processLoadSpritesCallbackQueue();
	}

	@Override
	public void onSecondElapsed(int secondsSinceStartup)
	{
		processLoadSpritesCallbackQueue();
	}

	/**
	 * Run all callbacks in the {@link #callbackQueue} as long as we have reached the login screen.
	 * This guarantees that the cache is loaded.
	 */
	private void processLoadSpritesCallbackQueue()
	{
		if (client.getGameState().ordinal() < GameState.LOGIN_SCREEN.ordinal())
		{
			return;
		}

		while (!callbackQueue.isEmpty())
		{
			callbackQueue.poll().accept(spriteManager);
		}
	}
}
