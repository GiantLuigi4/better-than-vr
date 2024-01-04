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
	
	/**
	 * Devices from ids are meant to be used only for a single frame
	 *
	 * @param id the device id
	 * @return an object representing the device
	 */
	public static Device deviceForId(int id) {
		switch (VRManager.getActiveMode()) {
			case STEAM_VR:
				return new SDevice(id, false);
		}
		return null;
	}
	
	public void tick() {
	}
}
