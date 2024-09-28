package com.starcallingassist.enums;

import com.starcallingassist.StarCallingAssistPlugin;
import java.awt.image.BufferedImage;
import lombok.Getter;
import net.runelite.client.util.ImageUtil;

@Getter
public enum TierImage
{
	UNKNOWN(null),
	TIER_1(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_1_30.png")),
	TIER_2(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_2_30.png")),
	TIER_3(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_3_30.png")),
	TIER_4(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_4_30.png")),
	TIER_5(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_5_30.png")),
	TIER_6(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_6_30.png")),
	TIER_7(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_7_30.png")),
	TIER_8(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_8_30.png")),
	TIER_9(ImageUtil.loadImageResource(StarCallingAssistPlugin.class, "/size_9_30.png"));

	private final BufferedImage image;

	public static TierImage getByTier(int tier)
	{
		final TierImage[] tiers = TierImage.values();

		if (tier < 0 || tier >= tiers.length)
		{
			return UNKNOWN;
		}

		return tiers[tier];
	}

	TierImage(BufferedImage image)
	{
		this.image = image;
	}
}
