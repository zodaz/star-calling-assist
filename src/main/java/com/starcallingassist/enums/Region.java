package com.starcallingassist.enums;

import com.starcallingassist.constants.RegionKeyName;

public enum Region
{
	ASGARNIA(RegionKeyName.KEY_ASGARNIA),
	DESERT(RegionKeyName.KEY_DESERT),
	FELDIP(RegionKeyName.KEY_FELDIP),
	FOSSIL(RegionKeyName.KEY_FOSSIL),
	FREMMENIK(RegionKeyName.KEY_FREMMENIK),
	GNOME(RegionKeyName.KEY_GNOME),
	KANDARIN(RegionKeyName.KEY_KANDARIN),
	KARAMJA(RegionKeyName.KEY_KARAMJA),
	KEBOS(RegionKeyName.KEY_KEBOS),
	KOUREND(RegionKeyName.KEY_KOUREND),
	MISTHALIN(RegionKeyName.KEY_MISTHALIN),
	MORYTANIA(RegionKeyName.KEY_MORYTANIA),
	TIRANNWN(RegionKeyName.KEY_TIRANNWN),
	VARLAMORE(RegionKeyName.KEY_VARLAMORE),
	WILDERNESS(RegionKeyName.KEY_WILDERNESS),
	UNKNOWN(RegionKeyName.KEY_UNKNOWN);

	public final String keyName;

	Region(String keyName)
	{
		this.keyName = keyName;
	}

	public String getKeyName()
	{
		return keyName;
	}
}
