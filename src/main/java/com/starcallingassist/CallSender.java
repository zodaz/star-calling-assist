package com.starcallingassist;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;

import static net.runelite.http.api.RuneLiteAPI.GSON;

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

    public Response sendCall(String username, int world, int tier, String location, int miners) throws IOException, IllegalArgumentException
    {
	boolean success = false;

	Request request = new Request.Builder()
		.url(starConfig.getEndpoint())
		.addHeader("authorization", starConfig.getAuthorization())
		.post(RequestBody.create(
			MediaType.parse("application/json"),
			// Doesn't include in-game name unless toggled on (default value is off)
			GSON.toJson(new CallData(starConfig.includeIgn() ? username : "", world, tier, location, miners))))
		.build();

	return okHttpClient.newCall(request).execute();
    }
}
