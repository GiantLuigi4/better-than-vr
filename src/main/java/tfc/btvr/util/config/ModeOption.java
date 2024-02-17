package tfc.btvr.util.config;

import tfc.btvr.lwjgl3.VRMode;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class ModeOption extends Config.Option {
	String name;
	VRMode value;
	
	public ModeOption(String name, VRMode value) {
		this.name = name;
		this.value = value;
	}
	
	protected void write(Properties properties) {
		properties.put(name, value.cfgName());
	}
	
	protected void read(ConfigHandler properties) {
		switch (properties.getString(name)) {
			case "steam":
				value = VRMode.STEAM_VR;
				break;
			case "oculus":
				value = VRMode.OCULUS_VR;
				break;
			case "none":
			case "off":
				value = VRMode.NONE;
				break;
		}
	}
	
	public VRMode get() {
		return value;
	}
}
