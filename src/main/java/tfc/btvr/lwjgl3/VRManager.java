package tfc.btvr.lwjgl3;

import net.minecraft.client.Minecraft;
import org.lwjgl.openvr.*;
import tfc.btvr.lwjgl3.common.Bindings;
import tfc.btvr.lwjgl3.openvr.VRControllerInput;

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
