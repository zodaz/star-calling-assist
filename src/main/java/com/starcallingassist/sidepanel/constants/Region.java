package com.starcallingassist.sidepanel.constants;

public enum Region
{
    ASGARNIA(RegionKeyName.KEY_ASGARNIA),
    KARAMJA(RegionKeyName.KEY_KARAMJA),
    FELDIP(RegionKeyName.KEY_FELDIP),
    FOSSIL(RegionKeyName.KEY_FOSSIL),
    FREMMENIK(RegionKeyName.KEY_FREMMENIK),
    KOUREND(RegionKeyName.KEY_KOUREND),
    KANDARIN(RegionKeyName.KEY_KANDARIN),
    KEBOS(RegionKeyName.KEY_KEBOS),
    DESERT(RegionKeyName.KEY_DESERT),
    MISTHALIN(RegionKeyName.KEY_MISTHALIN),
    MORYTANIA(RegionKeyName.KEY_MORYTANIA),
    GNOME(RegionKeyName.KEY_GNOME),
    TIRANNWN(RegionKeyName.KEY_TIRANNWN),
    WILDERNESS(RegionKeyName.KEY_WILDERNESS),
    UNKNOWN(RegionKeyName.KEY_UNKNOWN);

    public final String keyName;

    Region(String keyName) {
	this.keyName = keyName;
    }

    public String getKeyName() {
	return keyName;
    }
}
