package tfc.btvr.lwjgl3.openvr;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.*;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.generic.Device;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.math.MatrixHelper;
import tfc.btvr.util.config.Config;

public class SDevice extends Device {
	int index;
	
	public static final SDevice HEAD = new SDevice(0, false);
	public static final SDevice LEFT_HAND = new SDevice(VR.ETrackedControllerRole_TrackedControllerRole_LeftHand, true);
	public static final SDevice RIGHT_HAND = new SDevice(VR.ETrackedControllerRole_TrackedControllerRole_RightHand, true);
	
	VRControllerState state = VRControllerState.calloc();
	
	int role = -1;
	
	public SDevice(int index, boolean isRole) {
		if (isRole) {
			this.role = index;
			int cachedIndex = VRSystem.VRSystem_GetControllerRoleForTrackedDeviceIndex(index);
			this.index = cachedIndex;
		} else {
			this.index = index;
		}
	}
	
	public static SDevice getDeviceForRole(DeviceType role) {
		if (role == DeviceType.HEAD) return HEAD;
		
		switch (role) {
			case LEFT_HAND:
				return LEFT_HAND;
			case RIGHT_HAND:
				return RIGHT_HAND;
			case TREADMILL:
				return new SDevice(VR.ETrackedControllerRole_TrackedControllerRole_Treadmill, true);
			case INVALID:
				return new SDevice(VR.ETrackedControllerRole_TrackedControllerRole_Invalid, true);
			default:
				throw new RuntimeException("Unsupported device type " + role);
		}
	}
	
	public void tick() {
		if (role != -1)
			index = VRSystem.VRSystem_GetControllerRoleForTrackedDeviceIndex(role);
	}
	
	private final HmdMatrix34 matr = HmdMatrix34.calloc();
	
	// TODO: maybe use https://gery.casiez.net/1euro/ ?
	// https://gery.casiez.net/1euro/InteractiveDemo/
	public HmdMatrix34 getTrueMatrix() {
		TrackedDevicePose pose = VRManager.getPose(index);
		
		double pct = VRRenderManager.getPct();
		if (!Config.EXTRA_SMOOTH_ROTATION.get()) pct = 1;
		
		TrackedDevicePose p0 = TrackedDevicePose.calloc();
		// TODO: 4 frame window?
		VRCompositor.VRCompositor_GetLastPoseForTrackedDeviceIndex(index, p0, null);
		
		double[] cursedMatr = MatrixHelper.interpMatrix(pose.mDeviceToAbsoluteTracking(), p0.mDeviceToAbsoluteTracking(), pct);
		HmdMatrix34 cursed = HmdMatrix34.calloc();
		for (int i = 0; i < cursedMatr.length; i++)
			cursed.m(i, (float) cursedMatr[i]);
		
		return cursed;
	}
	
	public HmdMatrix34 getMatrix() {
		TrackedDevicePose pose = VRManager.getPose(index);
		
		double pct = VRRenderManager.getPct();
		if (!Config.EXTRA_SMOOTH_ROTATION.get()) pct = 1;
		
		TrackedDevicePose p0 = TrackedDevicePose.calloc();
		// TODO: 4 frame window?
		VRCompositor.VRCompositor_GetLastPoseForTrackedDeviceIndex(index, p0, null);
		
		double[] cursedMatr = MatrixHelper.interpMatrix(pose.mDeviceToAbsoluteTracking(), p0.mDeviceToAbsoluteTracking(), pct);
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		if (mc.thePlayer != null) {
			double delt = VRManager.getRotation(pct);
			
			cursedMatr = MatrixHelper.mul(
					cursedMatr, MatrixHelper.quatToMat(
							MatrixHelper.axisQuat(
									Math.toRadians(-delt),
									0, 1, 0
							)
					)
			);
		}
		
		HmdMatrix34 cursed = HmdMatrix34.calloc();
		for (int i = 0; i < cursedMatr.length; i++)
			cursed.m(i, (float) cursedMatr[i]);
		
		return cursed;
	}
	
	@Override
	protected void finalize() throws Throwable {
		matr.free();
		state.free();
		super.finalize();
	}
}
