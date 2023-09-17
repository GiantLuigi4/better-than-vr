package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.openvr.VR;

public enum DeviceType {
	HEAD(Integer.MAX_VALUE),
	LEFT_HAND(VR.ETrackedControllerRole_TrackedControllerRole_LeftHand),
	RIGHT_HAND(VR.ETrackedControllerRole_TrackedControllerRole_RightHand),
	TREADMILL(VR.ETrackedControllerRole_TrackedControllerRole_Treadmill),
	INVALID(VR.ETrackedControllerRole_TrackedControllerRole_Invalid),
	;
	
	private final int type;
	
	public int getID() {
		return type;
	}
	
	DeviceType(int type) {
		this.type = type;
	}
	
	public static DeviceType valueOf(int role) {
		for (DeviceType value : values()) {
			if (value.type == role) return value;
		}
		return null;
	}
}