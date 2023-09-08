package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VRSystem;
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
	
	@Override
	protected void finalize() throws Throwable {
		matr.free();
		super.finalize();
	}
}
