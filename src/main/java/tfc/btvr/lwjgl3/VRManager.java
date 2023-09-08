package tfc.btvr.lwjgl3;

import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.openvr.VRSystem;
import tfc.btvr.lwjgl3.openvr.Device;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class VRManager {
	private static TrackedDevicePose.Buffer buffer = TrackedDevicePose.calloc(VR.k_unMaxTrackedDeviceCount);
	
	public static void tick() {
		VRCompositor.VRCompositor_WaitGetPoses(buffer, null);
	}
	
	public static TrackedDevicePose getPose(int index) {
		return buffer.get(index);
	}
}
