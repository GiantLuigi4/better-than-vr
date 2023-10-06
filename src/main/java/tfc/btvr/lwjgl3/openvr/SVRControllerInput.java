package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.InputAnalogActionData;
import org.lwjgl.openvr.InputDigitalActionData;
import org.lwjgl.openvr.VRActiveActionSet;
import org.lwjgl.openvr.VRInput;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static tfc.btvr.lwjgl3.VRManager.genErrorMap;

public class SVRControllerInput {
	private static final HashMap<Integer, String> INPUT_ERRORS = genErrorMap("EVRInputError_VRInputError_");
	
	private static final HashMap<String, Long> inputs = new HashMap<>();
	
	private static final InputDigitalActionData actionData = InputDigitalActionData.create();
	private static final InputAnalogActionData actionDataAnalog = InputAnalogActionData.create();
	private static final ArrayList<VRActiveActionSet.Buffer> activeActionSet = new ArrayList<>();
	
	public static void tick() {
		for (VRActiveActionSet.Buffer vrActiveActionSets : activeActionSet) {
			if (vrActiveActionSets != null) {
				int res = VRInput.VRInput_UpdateActionState(vrActiveActionSets, vrActiveActionSets.sizeof());
				checkErr(res);
			}
		}
	}
	
	static {
		try {
			Thread.sleep(1000);
		} catch (Throwable ignored) {
		}
		
		// motion
		calcHandle("gameplay", "Move");
		calcHandle("gameplay", "Rotate");
		calcHandle("gameplay", "Jump");
		calcHandle("gameplay", "Crouch");
		// interaction
		calcHandle("gameplay", "Attack");
		calcHandle("gameplay", "UseItem");
		// ui
		calcHandle("gameplay", "Pause");
		calcHandle("gameplay", "OpenInventory");
		
		// mouse emulation
//		calcHandle("ui", "LeftClick");
//		calcHandle("ui", "RightClick");
		
		createSet("gameplay");
//		createSet("ui");
	}
	
	private static void createSet(String name) {
		LongBuffer longBuffer = BufferUtils.createLongBuffer(1);
		int handleErrorCode = VRInput.VRInput_GetActionSetHandle("/actions/" + name, longBuffer);
		checkErr(handleErrorCode);
		long actionSetHandle = longBuffer.get(0);
		
		VRActiveActionSet.Buffer vrActiveActionSets = VRActiveActionSet.create(1);
		vrActiveActionSets.ulActionSet(actionSetHandle);
		vrActiveActionSets.ulRestrictedToDevice(0); // both hands
		vrActiveActionSets.nPriority(0);
		activeActionSet.add(vrActiveActionSets);
	}
	
	private static long calcHandle(String type, String name) {
		return inputs.computeIfAbsent(type + "/" + name, (key) -> {
			LongBuffer longBuffer = BufferUtils.createLongBuffer(1);
			int handleErrorCode = VRInput.VRInput_GetActionHandle("/actions/" + type + "/in/" + name, longBuffer);
			checkErr(handleErrorCode);
			return longBuffer.get(0);
		});
	}
	
	public static boolean getInput(String type, String name) {
		checkErr(VRInput.VRInput_GetDigitalActionData(
				calcHandle(type, name),
				actionData,
				actionData.sizeof(),
				0
		));
		return actionData.bState();
	}
	
	public static float[] getJoystick(String type, String name) {
		checkErr(VRInput.VRInput_GetAnalogActionData(
				calcHandle(type, name),
				actionDataAnalog,
				actionData.sizeof(),
				0
		));
		return new float[]{
				actionDataAnalog.x(),
				actionDataAnalog.y()
		};
	}
	
	private static void checkErr(int handleErrorCode) {
		if (handleErrorCode != 0) System.out.println(INPUT_ERRORS.get(handleErrorCode));
	}
}
