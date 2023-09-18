package tfc.btvr.util.gestures.immersion;

import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.openvr.HmdMatrix34;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.util.gestures.Gesture;
import tfc.btvr.util.gestures.GestureControllers;

public class AttackGesture extends Gesture {
	boolean intersects(Entity entity, double[] coord, double[] look, Minecraft mc, double len) {
		return entity.bb.func_1169_a(
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
	public void recognize(GestureControllers controller, Minecraft mc, double avgMot, double avgAng, Device dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		if (avgMot < 0.15) return;
		if (avgAng < 0.1) return;
		
		ItemStack stack = mc.thePlayer.getHeldItem();
		boolean isSword = stack != null && stack.getItem() instanceof ItemToolSword;
		double len = isSword ? 1 : 0.25 / 2;
		
		double[] coord = VRHelper.playerRelative(dev.getTrueMatrix());
		double[] trace = VRHelper.getTraceVector(dev.getMatrix());
		double[] coordOld = VRHelper.playerRelative(prevMatrix);
		double[] traceOld = VRHelper.getTraceVector(prevRel);
		for (Entity entity : mc.theWorld.getLoadedEntityList().toArray(new Entity[0])) {
			if (entity == mc.thePlayer) continue;
			
			// TODO: force hand damage for off-hand
			if (intersects(entity, coord, trace, mc, len) && !intersects(entity, coordOld, traceOld, mc, len)) {
				mc.playerController.attackEntity(mc.thePlayer, entity);
			}
		}
	}
	
	@Override
	public void recognize(Minecraft mc, double avgMot, double avgAng, Device dev, DeviceType type, HmdMatrix34 prevMatrix, HmdMatrix34 prevRel) {
		// no-op
	}
}
