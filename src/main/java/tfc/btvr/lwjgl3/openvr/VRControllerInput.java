package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.InputAnalogActionData;
import org.lwjgl.openvr.InputDigitalActionData;
import org.lwjgl.openvr.VRActiveActionSet;
import org.lwjgl.openvr.VRInput;

import java.nio.LongBuffer;
import java.util.HashMap;

import static tfc.btvr.lwjgl3.VRManager.genErrorMap;

public class VRControllerInput {
	private static final HashMap<Integer, String> INPUT_ERRORS = genErrorMap("EVRInputError_VRInputError_");
	
	private static final HashMap<String, Long> inputs = new HashMap<>();
	
	private static final InputDigitalActionData actionData = InputDigitalActionData.create();
	private static final InputAnalogActionData actionDataAnalog = InputAnalogActionData.create();
	private static final VRActiveActionSet.Buffer activeActionSet;
	
	static {
		try {
			Thread.sleep(1000);
		} catch (Throwable ignored) {
		}
		
		calcHandle("gameplay", "Pause");
		calcHandle("gameplay", "Move");
		calcHandle("gameplay", "UseItem");
		calcHandle("gameplay", "OpenInventory");
		
		LongBuffer longBuffer = BufferUtils.createLongBuffer(1);
		int handleErrorCode = VRInput.VRInput_GetActionSetHandle("/actions/gameplay", longBuffer);
		checkErr(handleErrorCode);
		long actionSetHandle = longBuffer.get(0);
		
		activeActionSet = VRActiveActionSet.create(1);
		activeActionSet.ulActionSet(actionSetHandle);
		activeActionSet.ulRestrictedToDevice(0); // both hands
		activeActionSet.nPriority(0);
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
		if (activeActionSet != null) {
			int res = VRInput.VRInput_UpdateActionState(activeActionSet, activeActionSet.sizeof());
			checkErr(res);
		}
		
		checkErr(VRInput.VRInput_GetDigitalActionData(
				calcHandle(type, name),
				actionData,
				actionData.sizeof(),
				0
		));
		return actionData.bState();
	}
	
	public static float[] getJoystick(String type, String name) {
		if (activeActionSet != null) {
			int res = VRInput.VRInput_UpdateActionState(activeActionSet, activeActionSet.sizeof());
			checkErr(res);
		}
		
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
