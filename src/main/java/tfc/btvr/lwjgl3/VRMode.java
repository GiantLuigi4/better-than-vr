package tfc.btvr.lwjgl3;

public enum VRMode {
	STEAM_VR("steam"),
	OCULUS_VR("oculus"),
	NONE("off");
	
	String configName;
	
	VRMode(String configName) {
		this.configName = configName;
	}
	
	public String cfgName() {
		return configName;
	}
}
