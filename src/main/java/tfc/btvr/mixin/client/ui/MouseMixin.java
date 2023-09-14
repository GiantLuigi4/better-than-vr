package tfc.btvr.mixin.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.ScreenUtil;
import tfc.btvr.lwjgl3.VRManager;

import java.nio.ByteBuffer;

@Mixin(value = Mouse.class, remap = false)
public abstract class MouseMixin {
	@Shadow
	private static int eventButton;
	
	@Shadow
	private static boolean eventState;
	
	@Shadow
	private static int event_x;
	
	@Shadow
	private static int event_y;
	
	@Shadow
	private static int event_dx;
	
	@Shadow
	private static int event_dy;
	
	@Shadow
	private static int last_event_raw_x;
	
	@Shadow
	private static int last_event_raw_y;
	
	@Shadow
	private static ByteBuffer buttons;
	
	
	// TODO: GuiScreen gets the event coords before polling any events
	// TODO: WorldSelectScreen checks that the mouse is pressed using the isButtonDown method
	@Inject(at = @At("HEAD"), method = "next", cancellable = true)
	private static void prePollEvent(CallbackInfoReturnable<Boolean> cir) {
		ScreenUtil.Button[] sbuttons = new ScreenUtil.Button[]{
				ScreenUtil.left, ScreenUtil.right
		};
		for (ScreenUtil.Button button : sbuttons) {
			if (!button.event) continue;
			
			button.event = false;
			eventButton = button.id;
			eventState = button.down;
			event_x = button.x;
			event_y = button.y;
			event_dx = event_x - last_event_raw_x;
			event_dy = event_y - last_event_raw_y;
			last_event_raw_x = event_x;
			last_event_raw_y = event_y;
			cir.setReturnValue(true);
			
			return;
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getEventX", cancellable = true)
	private static void preGetEvX(CallbackInfoReturnable<Integer> cir) {
		ScreenUtil.Button[] sbuttons = new ScreenUtil.Button[]{
				ScreenUtil.left, ScreenUtil.right
		};
		for (ScreenUtil.Button button : sbuttons) {
			if (!button.event) continue;
			
			cir.setReturnValue(button.x);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getEventY", cancellable = true)
	private static void preGetEvY(CallbackInfoReturnable<Integer> cir) {
		ScreenUtil.Button[] sbuttons = new ScreenUtil.Button[]{
				ScreenUtil.left, ScreenUtil.right
		};
		for (ScreenUtil.Button button : sbuttons) {
			if (!button.event) continue;
			
			cir.setReturnValue(button.y);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "isButtonDown", cancellable = true)
	private static void preCheckMouse(int btn, CallbackInfoReturnable<Boolean> cir) {
		if (btn == 0) {
			if (ScreenUtil.left.down)
				cir.setReturnValue(true);
		} else if (btn == 2) {
			if (ScreenUtil.right.down)
				cir.setReturnValue(true);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getX", cancellable = true)
	private static void preGetX(CallbackInfoReturnable<Integer> cir) {
		if (!VRManager.inStandby) {
			Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
			GuiScreen scrn = mc.currentScreen;
			if (scrn != null) {
				VRScreenData data = (VRScreenData) scrn;
				
				double d = data.better_than_vr$mouseOverride()[0];
				if (!Double.isNaN(d)) cir.setReturnValue((int) (d * mc.resolution.width));
				return;
			}
			cir.setReturnValue(-1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getY", cancellable = true)
	private static void preGetY(CallbackInfoReturnable<Integer> cir) {
		if (!VRManager.inStandby) {
			Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
			GuiScreen scrn = mc.currentScreen;
			if (scrn != null) {
				VRScreenData data = (VRScreenData) scrn;
				
				double d = data.better_than_vr$mouseOverride()[1];
				if (!Double.isNaN(d)) cir.setReturnValue((int) (d * mc.resolution.height));
				return;
			}
			cir.setReturnValue(-1);
		}
	}
}
