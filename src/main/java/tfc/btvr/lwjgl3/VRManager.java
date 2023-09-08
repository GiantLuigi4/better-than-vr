package tfc.btvr.lwjgl3;

import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;

public class VRManager {
	private static TrackedDevicePose.Buffer buffer = TrackedDevicePose.calloc(VR.k_unMaxTrackedDeviceCount);
	
	public static void tick() {
		VRCompositor.VRCompositor_WaitGetPoses(buffer, null);
	}
}
