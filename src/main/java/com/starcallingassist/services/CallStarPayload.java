package com.starcallingassist.services;

import com.starcallingassist.objects.Star;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallStarPayload
{
	private final String sender;
	private final int world;
	private final int tier;
	private final String location;
	private final int miners;

	public CallStarPayload(String playerName, @Nonnull Star star, @Nonnull String location)
	{
		this(
			Optional.ofNullable(playerName).orElse(""),
			star.getWorld(),
			Optional.ofNullable(star.getTier()).orElse(0),
			location,
			Optional.ofNullable(star.getCurrentMiners()).orElse(-1)
		);
	}

	public String toCallout()
	{
		String callout = String.format("W%d T%d %s",
			world,
			tier,
			Objects.equals(location, "pdead") ? "dead (private)" : location
		);

		if (miners == -1 || tier == 0)
		{
			return callout;
		}

		return callout + String.format(" (%d Miners)", miners);
	}
}
