package com.starcallingassist.old.objects;

import com.starcallingassist.StarCallingAssistConfig;
import javax.inject.Inject;
import static net.runelite.http.api.RuneLiteAPI.GSON;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CallSender
{
	private class CallData
	{
		private final int world;
		private final int tier;
		private final int miners;
		private final String location;
		private final String sender;

		public CallData(String sender, int world, int tier, String location, int miners)
		{
			this.sender = sender;
			this.world = world;
			this.tier = tier;
			this.location = location;
			this.miners = miners;
		}
	}

	@Inject
	private StarCallingAssistConfig starConfig;
	@Inject
	private OkHttpClient okHttpClient;

	public void sendCall(String username, int world, int tier, String location, int miners, Callback callback) throws IllegalArgumentException
	{
		Request request = new Request.Builder()
			.url(starConfig.getEndpoint())
			.addHeader("authorization", starConfig.getAuthorization())
			.post(RequestBody.create(
				MediaType.parse("application/json"),
				// Doesn't include in-game name unless toggled on (default value is off)
				GSON.toJson(new CallData(starConfig.includeIgn() ? username : "", world, tier, location, miners))))
			.build();

		okHttpClient.newCall(request).enqueue(callback);
	}
}
