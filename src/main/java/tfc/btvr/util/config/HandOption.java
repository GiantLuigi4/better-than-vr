package tfc.btvr.util.config;

import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class HandOption extends Config.Option {
	String name;
	Hand def;
	Hand value;
	
	protected enum Hand {
		LEFT, OFF, MAIN, RIGHT
	}
	
	public HandOption(String name, Hand defaultV) {
		this.name = name;
		this.def = defaultV;
		this.value = defaultV;
	}
	
	protected void write(Properties properties) {
		properties.put(name, value.name().toLowerCase());
	}
	
	protected void read(ConfigHandler properties) {
		switch (properties.getString(name)) {
			case "left":
				value = Hand.LEFT;
				break;
			case "right":
				value = Hand.RIGHT;
				break;
			case "off":
				value = Hand.OFF;
				break;
//				case "main":
//					value = Hand.MAIN;
//					break;
			default:
				value = Hand.MAIN;
				break;
		}
	}
	
	public SDevice get() {
		switch (value) {
			case LEFT:
				return SDevice.getDeviceForRole(DeviceType.LEFT_HAND);
			case RIGHT:
				return SDevice.getDeviceForRole(DeviceType.RIGHT_HAND);
			case OFF:
				return SDevice.getDeviceForRole(Config.LEFT_HANDED.get() ? DeviceType.RIGHT_HAND : DeviceType.LEFT_HAND);
			default:
				return SDevice.getDeviceForRole(Config.LEFT_HANDED.get() ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND);
		}
	}
	
	public DeviceType getType() {
		switch (value) {
			case LEFT:
				return DeviceType.LEFT_HAND;
			case RIGHT:
				return DeviceType.RIGHT_HAND;
			case OFF:
				return Config.LEFT_HANDED.get() ? DeviceType.RIGHT_HAND : DeviceType.LEFT_HAND;
			default:
				return Config.LEFT_HANDED.get() ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND;
		}
	}
}
