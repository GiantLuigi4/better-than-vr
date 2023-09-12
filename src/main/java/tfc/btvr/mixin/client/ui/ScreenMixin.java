package tfc.btvr.mixin.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.openvr.Device;

@Mixin(value = GuiScreen.class, remap = false)
public class ScreenMixin implements VRScreenData {
	@Unique
	double[] myPos;
	@Unique
	double[] mouseOverride = new double[2];
	@Unique
	double rotation;
	@Unique
	double offset;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		if (mc == null) return;
		
		Device HEAD = new Device(0);
		
		double[] vec = new double[]{0, 0, 1};
		VRHelper.orientVector(HEAD, vec);
		
		double angle = Math.atan2(vec[0], vec[2]);
		rotation = angle;
		
		double[] crd = VRHelper.playerRelative(HEAD);
		if (mc != null && mc.thePlayer != null) {
			myPos = new double[]{
					crd[0] + mc.thePlayer.x,
					crd[1] + mc.thePlayer.bb.minY,
					crd[2] + mc.thePlayer.z
			};
		} else {
			myPos = new double[]{crd[0], crd[1], crd[2]};
		}
		
		offset = 3;
	}
	
	@Override
	public double better_than_vr$getOffset() {
		return offset;
	}
	
	@Override
	public double better_than_vr$horizontalAngle() {
		return rotation;
	}
	
	@Override
	public double[] better_than_vr$getPosition() {
		return myPos;
	}
	
	@Override
	public double[] better_than_vr$mouseOverride() {
		return mouseOverride;
	}
}
