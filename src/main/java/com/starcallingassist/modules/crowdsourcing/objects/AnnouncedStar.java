package com.starcallingassist.modules.crowdsourcing.objects;

import com.google.gson.JsonObject;
import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AnnouncedStar
{
	@Getter
	private final Star star;

	@Getter
	private final long updatedAt;

	public static AnnouncedStar fromJsonObject(JsonObject entry)
	{
		if (entry == null ||
			!entry.has("world") ||
			!entry.has("tier") ||
			!entry.has("calledLocation") ||
			!entry.has("calledBy") ||
			!entry.has("calledAt") ||
			!entry.has("location")
		)
		{
			return null;
		}

		return new AnnouncedStar(
			new Star(
				entry.get("world").getAsInt(),
				new StarLocation(entry.get("calledLocation").getAsString()),
				entry.get("tier").getAsInt(),
				entry.get("calledBy").getAsString()
			),
			entry.get("calledAt").getAsLong()
		);
	}

	public boolean isSuccessorTo(@Nonnull AnnouncedStar previous)
	{
		if (!previous.getStar().getWorld().equals(star.getWorld()))
		{
			return false;
		}

		return previous.getUpdatedAt() < updatedAt;
	}
}
