package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.openvr.*;
import tfc.btvr.lwjgl3.VRManager;

public class Device {
	int index;
	
	public static final Device HEAD = new Device(0);
	
	public Device(int index) {
		this.index = index;
	}
	
	public static Device getDeviceForRole(DeviceType role) {
		return new Device(VRSystem.VRSystem_GetControllerRoleForTrackedDeviceIndex(role.getID()));
	}
	
	private final HmdMatrix34 matr = HmdMatrix34.calloc();
	
	public HmdMatrix34 getMatrix() {
		TrackedDevicePose pose = VRManager.getPose(index);
		return pose.mDeviceToAbsoluteTracking();
	}
	
	VRControllerState state = VRControllerState.calloc();
	
	@Override
	protected void finalize() throws Throwable {
		matr.free();
		state.free();
		super.finalize();
	}
}
