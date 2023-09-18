package tfc.btvr.util.gestures.immersion;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HitResult;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.itf.VRController;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.util.gestures.Gesture;

public class MiningGesture extends Gesture {
	HitResult traceBlock(double[] coord, double[] look, Minecraft mc, double len) {
		return mc.theWorld.checkBlockCollisionBetweenPoints(
				Vec3d.createVector(
						coord[0] + mc.thePlayer.x,
						coord[1] + mc.thePlayer.bb.minY - mc.thePlayer.getHeadHeight(),
						coord[2] + mc.thePlayer.z
				),
				Vec3d.createVector(
						coord[0] + mc.thePlayer.x + look[0],
						coord[1] + mc.thePlayer.bb.minY - mc.thePlayer.getHeadHeight() + look[1],
						coord[2] + mc.thePlayer.z + look[2]
				)
		);
	}
	
	boolean inBlock(double[] coord, double[] look, Minecraft mc, double len) {
		return mc.theWorld.checkBlockCollisionBetweenPoints(
				Vec3d.createVector(
						coord[0] + mc.thePlayer.x,
						coord[1] + mc.thePlayer.bb.minY - mc.thePlayer.getHeadHeight(),
						coord[2] + mc.thePlayer.z
				),
				Vec3d.createVector(
						coord[0] + mc.thePlayer.x + look[0],
						coord[1] + mc.thePlayer.bb.minY - mc.thePlayer.getHeadHeight() + look[1],
						coord[2] + mc.thePlayer.z + look[2]
				)
		) != null;
	}
	
	@Override
	public void recognize(Minecraft mc, double avgMot, double avgAng, Device dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		if (avgMot < 0.115) return;
		if (avgAng < 0.05) return;
		
		ItemStack stack = mc.thePlayer.getHeldItem();
		boolean isTool = stack != null && stack.getItem() instanceof ItemTool;
		double len = isTool ? 1 : 0.25 / 2;
		
		double[] coord = VRHelper.playerRelative(dev.getTrueMatrix());
		double[] trace = VRHelper.getTraceVector(dev.getMatrix());
		double[] coordOld = VRHelper.playerRelative(prevMatrix);
		double[] traceOld = VRHelper.getTraceVector(prevRel);
		
		if (
				inBlock(coord, trace, mc, len) &&
						!inBlock(coordOld, traceOld, mc, len)
		) {
			HitResult result = traceBlock(coord, trace, mc, len);
			
			((VRController) mc.playerController).better_than_vr$activateVRMining(result);
			
			// TODO: this logic is painful (physically)
			// TODO: this logic seems to like to hit extra times
			for (int i = 0; i < 3; i++) {
				if (((VRController) mc.playerController).better_than_vr$isMining()) {
					if (mc.thePlayer.gamemode.doBlockBreakingAnim) {
						mc.playerController.mine(
								result.x, result.y, result.z,
								result.side
						);
					} else {
						mc.playerController.destroyBlock(
								result.x, result.y, result.z,
								result.side, mc.thePlayer
						);
					}
				}
			}
		}
	}
}
