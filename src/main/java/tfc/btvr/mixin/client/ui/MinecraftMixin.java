package tfc.btvr.mixin.client.ui;

import net.minecraft.client.GameResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.input.InputType;
import net.minecraft.client.input.controller.ControllerInput;
import net.minecraft.core.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	public GuiScreen currentScreen;
	
	@Shadow
	@Final
	public GameResolution resolution;
	
	@Shadow
	public InputType inputType;
	
	@Shadow
	private Timer timer;
	
	@Shadow
	public ControllerInput controllerInput;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
//		VRRenderManager.grabUI(false);
//
//		int width = resolution.scaledWidth;
//		int height = resolution.scaledHeight;
//
//		int mouseX = Mouse.getX() * width / resolution.width;
//		int mouseY = height - Mouse.getY() * height / resolution.height - 1;
//
//		if (currentScreen != null) {
//			GL11.glClear(256);
//			if (inputType == InputType.CONTROLLER) {
//				mouseX = (int) controllerInput.cursorX;
//				mouseY = (int) controllerInput.cursorY;
//			}
//
//			currentScreen.drawScreen(mouseX, mouseY, this.timer.partialTicks);
//			if (inputType == InputType.CONTROLLER && currentScreen != null) {
//				currentScreen.drawCursor();
//			}
//
//			if (currentScreen != null && currentScreen.particleRenderer != null) {
//				currentScreen.particleRenderer.render(this.timer.partialTicks);
//			}
//		}
//
//		VRRenderManager.releaseUI();
	}
}
