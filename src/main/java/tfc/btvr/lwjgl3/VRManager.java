package tfc.btvr.lwjgl3;

import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.*;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.Input;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.ArrayList;
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
		
		Input.getInput("gameplay", "Pause");
	}
	
	public static TrackedDevicePose getPose(int index) {
		return buffer.get(index);
	}
}
