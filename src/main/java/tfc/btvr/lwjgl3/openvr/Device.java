package tfc.btvr.lwjgl3.openvr;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.*;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.math.MatrixHelper;

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
	
	// TODO: maybe use https://gery.casiez.net/1euro/ ?
	// https://gery.casiez.net/1euro/InteractiveDemo/
	public HmdMatrix34 getTrueMatrix() {
		TrackedDevicePose pose = VRManager.getPose(index);
		
		TrackedDevicePose p0 = TrackedDevicePose.calloc();
		// TODO: 4 frame window?
		VRCompositor.VRCompositor_GetLastPoseForTrackedDeviceIndex(index, p0, null);
		
		double[] cursedMatr = MatrixHelper.interpMatrix(pose.mDeviceToAbsoluteTracking(),  p0.mDeviceToAbsoluteTracking(), 0.5);
		HmdMatrix34 cursed = HmdMatrix34.calloc();
		for (int i = 0; i < cursedMatr.length; i++)
			cursed.m(i, (float) cursedMatr[i]);
		
		return cursed;
	}
	
	public HmdMatrix34 getMatrix() {
		TrackedDevicePose pose = VRManager.getPose(index);
		
		TrackedDevicePose p0 = TrackedDevicePose.calloc();
		// TODO: 4 frame window?
		VRCompositor.VRCompositor_GetLastPoseForTrackedDeviceIndex(index, p0, null);
		
		double[] cursedMatr = MatrixHelper.interpMatrix(pose.mDeviceToAbsoluteTracking(),  p0.mDeviceToAbsoluteTracking(), 0.5);
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		if (mc.thePlayer != null) {
			double pct = VRRenderManager.getPct();
			if (!Config.EXTRA_SMOOTH_ROTATION.get()) pct = 1;
			
			double delt = mc.thePlayer.yRot * pct + (1 - pct) * mc.thePlayer.yRotO;
			
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
//		return pose.mDeviceToAbsoluteTracking();
	}
	
	VRControllerState state = VRControllerState.calloc();
	
	@Override
	protected void finalize() throws Throwable {
		matr.free();
		state.free();
		super.finalize();
	}
}
