package tfc.btvr.util.gestures.immersion;

import net.minecraft.client.Minecraft;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.util.gestures.Gesture;

public class EatingGesture extends Gesture {
	boolean intersects(AABB box, double[] coord, double[] look, Minecraft mc, double len) {
		return box.func_1169_a(
				Vec3d.createVector(
						coord[0] ,
						coord[1]  - mc.thePlayer.getHeadHeight(),
						coord[2]
				),
				Vec3d.createVector(
						coord[0] + look[0],
						coord[1]  - mc.thePlayer.getHeadHeight() + look[1],
						coord[2]  + look[2]
				)
		) != null;
	}
	
	@Override
	public void recognize(Minecraft mc, double avgMot, double avgAng, SDevice dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		ItemStack stack = mc.thePlayer.getHeldItem();
		boolean isFod = stack != null && stack.getItem() instanceof ItemFood;
		
		if (!isFod) return;
		
		double[] coord = VRHelper.playerRelative(dev.getTrueMatrix());
		double[] trace = VRHelper.getTraceVector(dev.getMatrix());
		double[] coordOld = VRHelper.playerRelative(prevMatrix);
		double[] traceOld = VRHelper.getTraceVector(prevRel);
		
		SDevice HEAD = SDevice.HEAD;
		double[] vec = new double[]{
				0, -0.5, -1
		};
		VRHelper.orientVector(HEAD, vec);
		double[] hrel = VRHelper.playerRelative(HEAD);
		for (int i = 0; i < vec.length; i++) vec[i] /= 4;
		
		double bxSz = 0.1;
		AABB box = new AABB(
				hrel[0] + vec[0] - bxSz,
				hrel[1] + vec[1] - bxSz,
				hrel[2] + vec[2] - bxSz,
				hrel[0] + vec[0] + bxSz,
				hrel[1] + vec[1] + bxSz,
				hrel[2] + vec[2] + bxSz
		);
		if (
				intersects(box, coord, trace, mc, 0.25) &&
				intersects(box, coordOld, traceOld, mc, 0.25)
		) {
			mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack);
		}
	}
}
