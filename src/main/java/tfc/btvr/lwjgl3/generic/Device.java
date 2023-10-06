package tfc.btvr.lwjgl3.generic;

import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.openvr.SDevice;

public class Device {
	public static Device getDeviceForRole(DeviceType role) {
		switch (VRManager.getActiveMode()) {
			case STEAM_VR:
				return SDevice.getDeviceForRole(role);
		}
		return null;
	}
	
	public static Device deviceForId(int id) {
		switch (VRManager.getActiveMode()) {
			case STEAM_VR:
				return new SDevice(id);
		}
		return null;
	}
}
