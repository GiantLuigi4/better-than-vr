package tfc.btvr.util.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiContainer;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiInventory;
import net.minecraft.client.gui.GuiInventoryCreative;
import net.minecraft.core.player.gamemode.Gamemode;
import org.lwjgl.input.Mouse;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.openvr.VRControllerInput;
import tfc.btvr.mixin.client.vr.selection.MinecraftAccessor;
import tfc.btvr.util.ScreenUtil;

import java.util.ArrayList;

public class Bindings {
	
	private static final ArrayList<VRBinding> typicalBindings = new ArrayList<>();
	
	// interaction controls
	private static final VRBinding LEFT_CLICK = new ButtonBinding("gameplay", "Attack", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		((MinecraftAccessor) mc).invokeClickMouse(0, true, false);
	}, null, null);
	private static final VRBinding RIGHT_CLICK = new ButtonBinding("gameplay", "UseItem", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		((MinecraftAccessor) mc).invokeClickMouse(1, true, false);
	}, null, null);
	
	// hotbar controls
	private static final VRBinding NEXT_SLOT = new ButtonBinding("gameplay", "HotbarRight", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		mc.thePlayer.inventory.changeCurrentItem(-1);
	}, null, null);
	private static final VRBinding PREV_SLOT = new ButtonBinding("gameplay", "HotbarLeft", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		mc.thePlayer.inventory.changeCurrentItem(1);
	}, null, null);
	
	// gui controls
	private static final VRBinding PAUSE_GAME = new ButtonBinding("gameplay", "Pause", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		
		if (mc.currentScreen == null) mc.displayGuiScreen(new GuiIngameMenu());
		else mc.displayGuiScreen(null);
	}, null, null);
	private static final VRBinding OPEN_INV = new ButtonBinding("gameplay", "OpenInventory", () -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		
		if (mc.currentScreen == null) {
			if (mc.thePlayer.gamemode == Gamemode.creative)
				mc.displayGuiScreen(new GuiInventoryCreative(mc.thePlayer));
			else mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
		} else if (mc.currentScreen instanceof GuiContainer) mc.displayGuiScreen(null);
	}, null, null);
	
	private static boolean rotateActive = false;
	// motion controls
	private static final VRBinding ROTATE = new PositionBinding("gameplay", "Rotate", (x, y) -> {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		
		if (Config.SMOOTH_ROTATION.get()) {
			mc.thePlayer.yRot += (float) ((Config.ROTATION_SPEED.get() / 4) * x);
		} else {
			boolean rotating = x != 0;
			if (rotating && !rotateActive)
				mc.thePlayer.yRot += (float) (Config.ROTATION_SPEED.get() * Math.signum(x));
			rotateActive = rotating;
		}
	});
	
	public static void renderTick(Minecraft mc) {
		if (mc.currentScreen != null) {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, true, VRControllerInput.getInput("gameplay", "UseItem"));
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, false, VRControllerInput.getInput("gameplay", "Attack"));
		} else {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, true, false);
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, false, false);
		}
		
		PAUSE_GAME.tick();
	}
	
	public static void postTick(Minecraft mc) {
		ROTATE.tick();
	}
	
	public static void primaryTick(Minecraft mc) {
		OPEN_INV.tick();
		
		if (mc.currentScreen == null) for (VRBinding typicalBinding : typicalBindings) typicalBinding.tick();
		else for (VRBinding typicalBinding : typicalBindings) typicalBinding.forceRelease();
	}
	
	public static void addBinding(String translation, VRBinding binding) {
		addSpecial(translation, binding);
		typicalBindings.add(binding);
	}
	
	public static void addSpecial(String translation, VRBinding binding) {
	}
	
	static {
		addBinding("btvr.gameplay.attack", LEFT_CLICK);
		addBinding("btvr.gameplay.use_item", RIGHT_CLICK);
		
		addBinding("btvr.gameplay.hotbar_right", NEXT_SLOT);
		addBinding("btvr.gameplay.hotbar_left", PREV_SLOT);
		
		addSpecial("btvr.gameplay.rotate", ROTATE);
		addSpecial("btvr.gameplay.open_inv", OPEN_INV);
		addSpecial("btvr.gameplay.pause", PAUSE_GAME);
	}
}
