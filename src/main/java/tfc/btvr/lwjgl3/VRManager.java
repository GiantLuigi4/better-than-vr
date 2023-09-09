package tfc.btvr.lwjgl3;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.*;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.openvr.Input;
import tfc.btvr.math.MatrixHelper;
import tfc.btvr.math.VecMath;

import java.lang.reflect.Field;
import java.util.HashMap;

public class VRManager {
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
	
	public static void tick() {
		VRCompositor.VRCompositor_WaitGetPoses(buffer, null);
		
		while (true) {
			VREvent ev = VREvent.calloc();
			boolean v = VRSystem.VRSystem_PollNextEvent(ev);
			String type = EV_TYPES.get(ev.eventType());
			if (type != null && !type.equals("None"))
				System.out.println(EV_TYPES.get(ev.eventType()));
			
			if (!v)
				break;
		}
	}
	
	public static void tickGame(Minecraft mc) {
		if (mc.thePlayer != null) {
			float[] m = Input.getJoystick("gameplay", "Move");
			m[1] = -m[1];
			
			double len = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
			
			HmdMatrix34 matr = Config.MOTION_HAND.get().getMatrix();
			
			double[] res = new double[3];
			MatrixHelper.mulMatr(
					m[0], 0, m[1],
					
					matr.m(0), matr.m(1), matr.m(2), 0,
					matr.m(4), matr.m(5), matr.m(6), 0,
					matr.m(8), matr.m(9), matr.m(10), 0,
					
					res
			);
			res[1] = 0;
			VecMath.normalize(res);
			
			if (m[0] != 0 || m[1] != 0) {
				mc.thePlayer.xd += res[0] * 0.2f * len;
				mc.thePlayer.zd += res[2] * 0.2f * len;
			}
		}
	}
	
	public static TrackedDevicePose getPose(int index) {
		return buffer.get(index);
	}
}
