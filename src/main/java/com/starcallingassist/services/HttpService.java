package com.starcallingassist.services;

import com.starcallingassist.StarCallingAssistConfig;
import javax.inject.Inject;
import static net.runelite.http.api.RuneLiteAPI.GSON;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpService
{
	@Inject
	private StarCallingAssistConfig starConfig;

	@Inject
	private OkHttpClient okHttpClient;

	public void post(CallStarPayload payload, Callback callback) throws IllegalArgumentException
	{
		Request request = new Request.Builder()
			.url(starConfig.getEndpoint())
			.addHeader("authorization", starConfig.getAuthorization())
			.post(RequestBody.create(MediaType.parse("application/json"), GSON.toJson(payload)))
			.build();

		okHttpClient.newCall(request).enqueue(callback);
	}
}
