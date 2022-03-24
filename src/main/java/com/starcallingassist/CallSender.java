package com.starcallingassist;

import lombok.ToString;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.runelite.http.api.RuneLiteAPI.GSON;

public class CallSender
{

    @ToString
    private class CallData{
	private final String timestamp;
	private final long id;
	private final String chatType;
	private final String chatName;
	private final String sender;
	private final String message;

	public CallData(String sender, String chatName, String message)
	{
	    timestamp = ZonedDateTime.now(Clock.systemUTC()).toString();
	    long t_id = new Random().nextLong();
	    if (t_id < 0)
		t_id *= -1;
	    id = t_id;
	    chatType = "FRIENDS";
	    this.chatName = chatName;
	    this.sender = sender;
	    this.message = message;
	}
    }
    private final StarCallingAssistConfig config;

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
	    .connectTimeout(5, TimeUnit.SECONDS)
	    .writeTimeout(3, TimeUnit.SECONDS)
	    .readTimeout(2, TimeUnit.SECONDS)
	    .cache(null)
	    .build();

    private String endpoint;

    public void updateConfig() {
	endpoint = config.getEndpoint();
    }

    @Inject CallSender(StarCallingAssistConfig config)
    {
	this.config = config;
    }

    public boolean sendCall(String username, String world, String tier, String location) throws IOException
    {
	List<CallData> data = new ArrayList<>();
	data.add(new CallData(username, "plugin", world + " " + tier + " " + location));
	Request request = new Request.Builder()
		.url(endpoint)
		.addHeader("Authorization", "none")
		.post(RequestBody.create(MediaType.parse("application/json"), GSON.toJson(data)))
		.build();

	return okHttpClient.newCall(request).execute().isSuccessful();
    }

}
