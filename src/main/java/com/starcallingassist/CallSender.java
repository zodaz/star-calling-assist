package com.starcallingassist;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import javax.inject.Inject;
import java.io.IOException;

import static net.runelite.http.api.RuneLiteAPI.GSON;

public class CallSender
{
    private class CallData
    {
	private final int world;
	private final int tier;
	private final String location;
	private final String sender;

	public CallData(String sender, int world, int tier, String location)
	{
	    this.sender = sender;
	    this.world = world;
	    this.tier = tier;
	    this.location = location;
	}
    }

    private String endpoint;

    private final StarCallingAssistConfig config;
    private final OkHttpClient okHttpClient;

    public void updateConfig() {
	endpoint = config.getEndpoint();
    }

    @Inject CallSender(StarCallingAssistConfig config, OkHttpClient okHttpClient)
    {
	this.config = config;
	this.okHttpClient = okHttpClient;
    }

    public boolean sendCall(String username, int world, int tier, String location) throws IOException, IllegalArgumentException
    {
	Request request = new Request.Builder()
		.url(endpoint)
		.post(RequestBody.create(MediaType.parse("application/json"), GSON.toJson(new CallData(username, world, tier, location))))
		.build();

	return okHttpClient.newCall(request).execute().isSuccessful();
    }
}
