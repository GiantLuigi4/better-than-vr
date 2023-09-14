package tfc.btvr.lwjgl3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiContainer;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiInventory;
import net.minecraft.client.gui.GuiInventoryCreative;
import net.minecraft.core.player.gamemode.Gamemode;
import org.lwjgl.input.Mouse;
import org.lwjgl.openvr.*;
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
	
	public static boolean inStandby = true;
	
	private static boolean inventory = false;
	private static boolean pauseToggled = false;
	private static boolean invToggled = false;
	
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
		
		if (VRControllerInput.getInput("gameplay", "Pause")) {
			if (!pauseToggled) {
				if (mc.currentScreen == null) mc.displayGuiScreen(new GuiIngameMenu());
				else mc.displayGuiScreen(null);
			}
			
			pauseToggled = true;
		} else pauseToggled = false;
		
		inventory = VRControllerInput.getInput("gameplay", "OpenInventory");
		
		if (mc.currentScreen != null) {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, true, VRControllerInput.getInput("gameplay", "UseItem"));
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, false, VRControllerInput.getInput("gameplay", "Attack"));
		} else {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, true, false);
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, false, false);
		}
	}
	
	public static void tickGame(Minecraft mc) {
		if (inventory && !invToggled) {
			
			if (mc.currentScreen == null) {
				if (mc.thePlayer.gamemode == Gamemode.creative)
					mc.displayGuiScreen(new GuiInventoryCreative(mc.thePlayer));
				else mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
			} else if (mc.currentScreen instanceof GuiContainer) mc.displayGuiScreen(null);
		}
		
		invToggled = inventory;
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
