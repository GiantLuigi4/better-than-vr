package tfc.btvr.lwjgl3;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.*;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.VRControllerInput;
import tfc.btvr.math.VecMath;
import tfc.btvr.util.controls.Bindings;
import tfc.btvr.util.gestures.GestureControllers;

import java.lang.reflect.Field;
import java.util.HashMap;

public class VRManager {
	public static double ox, oz;
	
	private static TrackedDevicePose.Buffer buffer = TrackedDevicePose.calloc(VR.k_unMaxTrackedDeviceCount);
	
	private static final HashMap<Integer, String> EV_TYPES = genErrorMap("EVREventType_VREvent_");
	
	public static HashMap<Integer, String> genErrorMap(String type) {
		HashMap<Integer, String> map = new HashMap<>();
		
		for (Field field : VR.class.getFields()) {
			if (field.getName().startsWith(type)) {
				try {
					map.put((Integer) field.get(null), field.getName().substring(type.length()));
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		}
		
		return map;
	}
	
	// steamvr defaults to not being in standby
	public static boolean inStandby = false;
	
	public static void tick() {
		VRCompositor.VRCompositor_WaitGetPoses(buffer, null);
		
		VRControllerInput.tick();
		
		while (true) {
			VREvent ev = VREvent.calloc();
			boolean v = VRSystem.VRSystem_PollNextEvent(ev);
			if (ev.eventType() == 107)
				inStandby = false;
			else if (ev.eventType() == 106)
				inStandby = true;

//			String type = EV_TYPES.get(ev.eventType());
//			if (type != null && !type.equals("None")) {
//				System.out.println(EV_TYPES.get(ev.eventType()));
//			}
			
			if (!v)
				break;
		}
		
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		
		Bindings.renderTick(mc);
	}
	
	public static void tickGame(Minecraft mc) {
		Bindings.primaryTick(mc);
		
		if (mc.thePlayer != null) {
			GestureControllers.getHeadController().tick(mc);
			GestureControllers.getMainHandController().tick(mc);
			GestureControllers.getOffHandController().tick(mc);
		}
	}
	
	public static void postTick(Minecraft mc) {
		Bindings.postTick(mc);
		
		// lol I should clean this up
		Device head = Device.HEAD;
		HmdMatrix34 matr34 = head.getTrueMatrix();
		
		double[] pos = VRHelper.getPosition(matr34);
		double[] look = VRHelper.getTraceVector(matr34);
		
		double tx = pos[0] - look[0] / 4;
		double tz = pos[2] - look[2] / 4;
		
		double x = mc.thePlayer.x;
		double z = mc.thePlayer.z;
		
		double y = mc.thePlayer.y;
		
		double dx = (tx - ox);
		double dz = (tz - oz);
		
		double[] res = VecMath.rotate(new double[]{dx, dz}, Math.toRadians(mc.thePlayer.yRot));
		dx = res[0];
		dz = res[1];
		
		mc.thePlayer.move(dx, mc.thePlayer.yd, dz);
		mc.thePlayer.y = y;
		
		mc.thePlayer.xo = mc.thePlayer.xOld -= (x - mc.thePlayer.x);
		mc.thePlayer.zo = mc.thePlayer.zOld -= (z - mc.thePlayer.z);

		ox = tx;
		oz = tz;
	}
	
	public static float[] getVRMotion() {
		float[] m = VRControllerInput.getJoystick("gameplay", "Move");
		m[1] = -m[1];
		return m;
	}
	
	public static TrackedDevicePose getPose(int index) {
		return buffer.get(index);
	}
}
