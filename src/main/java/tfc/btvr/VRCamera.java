package tfc.btvr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.math.VecMath;
import tfc.btvr.model.VRModel;

import java.nio.FloatBuffer;

public class VRCamera {
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
	
	protected static float[] toArray(HmdMatrix34 matr) {
		return new float[]{
				matr.m(0), matr.m(1), matr.m(2), 0,
				matr.m(4), matr.m(5), matr.m(6), 0,
				matr.m(8), matr.m(9), matr.m(10), 0,
				0, 0, 0, 1,
		};
	}
	
	public static void apply(float pct, ICamera instance, float farDist) {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		
		Eye active = Eye.getActiveEye();
		int id = 0;
		if (active != null)
			id = active.id;
		
		float[] data;
		if (active != null || mc.theWorld == null) {
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			
			HmdMatrix44 matr = Eye.getProjectionMatrix(id, 0.1f, farDist);
			
			data = new float[]{
					matr.m(0), matr.m(4), matr.m(8), matr.m(12),
					matr.m(1), matr.m(5), matr.m(9), matr.m(13),
					matr.m(2), matr.m(6), matr.m(10), matr.m(14),
					matr.m(3), matr.m(7), matr.m(11), matr.m(15),
			};
			buffer.put(data);
			buffer.flip();
			GL11.glLoadMatrix(buffer);
//			GL11.glScaled(1, 1, -1);
		}
		
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		
		SDevice head = SDevice.HEAD;
		
		if (active != null) {
			HmdMatrix34 matr32 = Eye.getTranslationMatrix(id);
			
			float mul = -1;
			data = new float[]{
					matr32.m(0), matr32.m(4), matr32.m(8), 0,
					matr32.m(1), matr32.m(5), matr32.m(9), 0,
					matr32.m(2), matr32.m(6), matr32.m(10), 0,
					matr32.m(3) * mul, matr32.m(7) * mul, matr32.m(11) * mul, 1,
			};
			buffer.put(data);
			buffer.flip();
			GL11.glMultMatrix(buffer);
		}
		
		HmdMatrix34 matr = head.getTrueMatrix();
		
		data = toArray(matr);
		buffer.put(data);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
		
		GL11.glTranslated(matr.m(3) * -1, -matr.m(7), matr.m(11) * -1);
		
		if (instance != null && mc.thePlayer != null) {
			GL11.glTranslated(VRManager.ox, 0, VRManager.oz);
			GL11.glRotated(VRManager.getRotation(1), 0, 1, 0);
			
			GL11.glTranslated(0, mc.thePlayer.heightOffset, 0);
			GL11.glTranslated(0, -mc.thePlayer.getHeadHeight(), 0);
		}
	}
	
	public static final VRModel normal = new VRModel();
	
	private static float armScl = 1 / 32f;
	
	public static void draw(VRModel model, EntityPlayer player, boolean left, float scale) {
		model.draw(player, left, scale);
	}
	
	public static void renderPlayer(EntityPlayer thePlayer, float renderPartialTicks, RenderGlobal renderGlobal) {
		renderPlayer(false, thePlayer, renderPartialTicks, renderGlobal);
	}
	
	public static void handMatrix(EntityPlayer player, double pct, boolean left, SDevice device) {
		HmdMatrix34 matr = device.getMatrix();
		float[] data = new float[]{
				matr.m(0), matr.m(4), matr.m(8), 0,
				matr.m(1), matr.m(5), matr.m(9), 0,
				matr.m(2), matr.m(6), matr.m(10), 0,
				matr.m(3), matr.m(7), matr.m(11), 1,
		};
		buffer.put(data);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
		
		handMatrix(player, pct, left);
	}
	
	public static void handMatrix(EntityPlayer player, double pct, boolean left) {
		GL11.glScaled(left ? 1 : -1, -1, -1);
		GL11.glRotatef(90, 1, 0, 0);
		GL11.glRotatef(180, 0, 1, 0);
		
		GL11.glTranslated(0, -6 * armScl, 0);
		if (player != null && left == Config.LEFT_HANDED.get()) {
			double prog = player.swingProgress;
			if (prog == 0 && player.prevSwingProgress != 0) prog = 1;
			double interp = prog * pct + player.prevSwingProgress * (1 - pct);
			GL11.glRotatef(
					(float) Math.cos(interp * Math.PI * 2) * 18,
					1, 0, 0
			);
		}
	}
	
	protected static void drawItem(EntityLiving entity, Minecraft mc, boolean leftHanded) {
		ItemStack itemstack = entity.getHeldItem();
		if (itemstack != null) {
			GL11.glPushMatrix();
			
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			float f2;
			if (itemstack.itemID < Block.blocksList.length && ((BlockModel) BlockModelDispatcher.getInstance().getDispatch(Block.blocksList[itemstack.itemID])).shouldItemRender3d()) {
				f2 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f2 *= 0.5F;
				GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
			} else if (itemstack.itemID == Item.toolBow.id) {
				f2 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if (Item.itemsList[itemstack.itemID].isFull3D()) {
				f2 = 0.625F;
				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				if (
						Item.itemsList[itemstack.itemID] instanceof ItemTool ||
								Item.itemsList[itemstack.itemID] instanceof ItemToolSword
				) {
					GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(5.0F, 0.0F, 1.0F, 0.0F);
					GL11.glTranslated(0.025, 0, 0);
				}
			} else {
				f2 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}
			
			EntityRenderDispatcher.instance.itemRenderer.renderItem(entity, itemstack);
			GL11.glPopMatrix();
		}
	}
	
	public static void renderPlayer(boolean menu, EntityPlayer thePlayer, float renderPartialTicks, RenderGlobal renderGlobal) {
		SDevice rightHand = SDevice.getDeviceForRole(DeviceType.RIGHT_HAND);
		SDevice leftHand = SDevice.getDeviceForRole(DeviceType.LEFT_HAND);
		
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		ICamera camera = mc.activeCamera;
		
		if (mc.theWorld != null && camera != null && camera.showPlayer())
			return;
		
		if (thePlayer != null) {
			EntityRenderDispatcher dispatcher = EntityRenderDispatcher.instance;
			if (dispatcher.renderEngine == null) dispatcher.renderEngine = mc.renderEngine;
			
			PlayerRenderer renderer = (PlayerRenderer) (LivingRenderer) dispatcher.getRenderer(thePlayer);
			renderer.loadEntityTexture(thePlayer);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			menu = true;
		}
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glColorMask(true, true, true, true);
		float brightness = 1.0f;
		if (!mc.fullbright && !menu)
			brightness = thePlayer.getBrightness(renderPartialTicks);
		GL11.glColor3f(brightness, brightness, brightness);
		
		GL11.glPushMatrix();
		if (!menu && camera != null) {
			GL11.glRotated(-VRManager.getRotation(1), 0, 1, 0);
			GL11.glTranslated(-VRManager.ox, 0, -VRManager.oz);
			GL11.glRotated(VRManager.getRotation(1), 0, 1, 0);
			
			GL11.glTranslated(0, -mc.thePlayer.heightOffset + mc.thePlayer.getHeadHeight(), 0);
			
			GL11.glTranslated(-camera.getX(), -camera.getY(), -camera.getZ());
			GL11.glTranslated(thePlayer.x, thePlayer.y, thePlayer.z);
		}
		
		
		// right hand
		GL11.glPushMatrix();
		handMatrix(thePlayer, renderPartialTicks, false, rightHand);
		draw(normal, thePlayer, false, armScl);
//		draw(mdl.bipedRightArmOverlay);
		GL11.glPopMatrix();
		
		// left hand
		GL11.glPushMatrix();
		handMatrix(thePlayer, renderPartialTicks, true, leftHand);
		draw(normal, thePlayer, true, armScl);
//		draw(mdl.bipedLeftArmOverlay);
		GL11.glPopMatrix();
		
		
		if (thePlayer != null) {
			boolean leftHanded = Config.LEFT_HANDED.get();
			
			// UI rendering
			GL11.glPushMatrix();
			handMatrix(thePlayer, renderPartialTicks, !leftHanded, leftHanded ? rightHand : leftHand);
			
			AABB UIQuad = new AABB(-2, -1, 0, 2, 1, 0);
			GL11.glColor4f(1, 1, 1, 1);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			
			VRRenderManager.bindUI();
			GL11.glRotated(-90, 0, 0, 1);
			GL11.glScaled(armScl, armScl, armScl);
			GL11.glTranslated(0, 0, -2.001);
			GL11.glScaled(14, 14, 1);
			GL11.glTranslated(0, 0.857, 0);
			if (leftHanded) GL11.glScaled(-1, 1, 1);
			Tessellator.instance.startDrawingQuads();
			
			Tessellator.instance.addVertexWithUV(UIQuad.minX, UIQuad.minY, 0, 1, 0);
			Tessellator.instance.addVertexWithUV(UIQuad.maxX, UIQuad.minY, 0, 0, 0);
			Tessellator.instance.addVertexWithUV(UIQuad.maxX, UIQuad.maxY, 0, 0, 1);
			Tessellator.instance.addVertexWithUV(UIQuad.minX, UIQuad.maxY, 0, 1, 1);
			Tessellator.instance.draw();
			
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
			
			
			// held item rendering
			GL11.glPushMatrix();
			
			handMatrix(thePlayer, renderPartialTicks, leftHanded, leftHanded ? leftHand : rightHand);
			
			// TODO: fix lighting
			GL11.glScaled(1, 1, -1);
			
			GL11.glRotated(-90, 0, 0, 1);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glRotated(-4.5, 0, 0, 1);
			GL11.glTranslated(0.04, -0.15, -0.025);
			GL11.glScaled(armScl * 16, armScl * 16, armScl * 16);
			
			drawItem(thePlayer, mc, leftHanded);
			GL11.glPopMatrix();
		}
		
		
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	private static Vec3d a(double[] coord) {
		return Vec3d.createVector(coord[0], coord[1], coord[2]);
	}
	
	private static Vec3d a(double[] coord0, double[] coord) {
		return Vec3d.createVector(coord0[0] + coord[0], coord0[1] + coord[1], coord0[2] + coord[2]);
	}
	
	public static void drawUI(Minecraft mc, float renderPartialTicks) {
		VRScreenData data = (VRScreenData) mc.currentScreen;
		if (data == null) return;
		
		Lighting.disable();
		GL11.glPushMatrix();
		
		if (mc.thePlayer != null && mc.activeCamera != null) {
			GL11.glTranslated(
					-mc.activeCamera.getX(renderPartialTicks),
					-mc.activeCamera.getY(renderPartialTicks),
					-mc.activeCamera.getZ(renderPartialTicks)
			);
		}
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		double angle = Math.toDegrees(data.better_than_vr$horizontalAngle()) - 180;
		Vec3d UIPos = a(data.better_than_vr$getPosition());
		double offset = data.better_than_vr$getOffset();
		AABB UIQuad = new AABB(-2, -1, 0, 2, 1, 0);
		
		GL11.glTranslated(UIPos.xCoord, UIPos.yCoord, UIPos.zCoord);
		
		GL11.glRotated(angle, 0, 1, 0);
		GL11.glTranslated(0, 0, offset);
		
		GL11.glColor4f(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		VRRenderManager.bindGUI();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertexWithUV(UIQuad.minX, UIQuad.minY, 0, 1, 0);
		Tessellator.instance.addVertexWithUV(UIQuad.maxX, UIQuad.minY, 0, 0, 0);
		Tessellator.instance.addVertexWithUV(UIQuad.maxX, UIQuad.maxY, 0, 0, 1);
		Tessellator.instance.addVertexWithUV(UIQuad.minX, UIQuad.maxY, 0, 1, 1);
		Tessellator.instance.draw();
		
		
		Vec3d pos;
		if (mc.thePlayer != null) {
			pos = a(
					new double[]{mc.thePlayer.x, mc.thePlayer.bb.minY, mc.thePlayer.z},
					VRHelper.playerRelative(Config.TRACE_HAND.get())
			);
		} else {
			pos = a(VRHelper.playerRelative(Config.TRACE_HAND.get()));
		}
		Vec3d look = a(VRHelper.getTraceVector(Config.TRACE_HAND.get()));
		
		pos = pos.subtract(UIPos);
		
		double[] rot = VecMath.rotate(new double[]{pos.xCoord, pos.zCoord}, Math.toRadians(angle + 180));
		pos = Vec3d.createVector(rot[0], pos.yCoord, rot[1]);
		pos = pos.addVector(0, 0, -offset);
		
		rot = VecMath.rotate(new double[]{look.xCoord, look.zCoord}, Math.toRadians(angle + 180));
		look = Vec3d.createVector(rot[0], look.yCoord, rot[1]);
		
		HitResult res = UIQuad.func_1169_a(pos, pos.addVector(look.xCoord * -10, look.yCoord * -10, look.zCoord * -10));
		if (res != null) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
			double x = res.location.xCoord;
			double y = -res.location.yCoord;
			
			x = -res.location.xCoord;
			if (Double.isNaN(data.better_than_vr$mouseOverride()[0])) {
				data.better_than_vr$mouseOverride()[0] = (x - UIQuad.minX) / (UIQuad.maxX - UIQuad.minX);
				data.better_than_vr$mouseOverride()[1] = (y - UIQuad.minY) / (UIQuad.maxY - UIQuad.minY);
			} else {
				double pct = 0.125;
				
				data.better_than_vr$mouseOverride()[0] =
						data.better_than_vr$mouseOverride()[0] * (1 - pct) +
								((x - UIQuad.minX) / (UIQuad.maxX - UIQuad.minX)) * pct;
				data.better_than_vr$mouseOverride()[1] =
						data.better_than_vr$mouseOverride()[1] * (1 - pct) +
								((y - UIQuad.minY) / (UIQuad.maxY - UIQuad.minY)) * pct;
			}
			
			x = -data.better_than_vr$mouseOverride()[0] * (UIQuad.maxX - UIQuad.minX) - UIQuad.minX;
			y = data.better_than_vr$mouseOverride()[1] * (UIQuad.maxY - UIQuad.minY) + UIQuad.minY;
			
			double rad = 0.01;
			
			int qual = 64;
			double d = 360d / qual;
			GL11.glDepthFunc(GL11.GL_ALWAYS);
			Tessellator.instance.startDrawing(GL11.GL_TRIANGLES);
			for (int i = 0; i < qual; i++) {
				double s = Math.sin(Math.toRadians(i * d)) * rad;
				double c = Math.cos(Math.toRadians(i * d)) * rad;
				
				Tessellator.instance.addVertex(x + s, y + c, 0);
				Tessellator.instance.addVertex(x, y, 0);
				
				s = Math.sin(Math.toRadians((i + 1) * d)) * rad;
				c = Math.cos(Math.toRadians((i + 1) * d)) * rad;
				Tessellator.instance.addVertex(x + s, y + c, 0);
			}
			Tessellator.instance.draw();
			GL11.glDepthFunc(515);
			
			// TODO: I'd like to draw a line between the hand and the crosshair
//			GL11.glLineWidth(1);
//			Tessellator.instance.startDrawing(GL11.GL_LINES);
//			Tessellator.instance.addVertexWithUV(x, y, 0, 1, 0);
//			double[] hand = VRHelper.playerRelative(Config.TRACE_HAND.get());
//			double[] oset = new double[]{0, offset};
//			oset = VecMath.rotate(oset, Math.toRadians(angle + 27));
//			Tessellator.instance.addVertexWithUV(
//					-UIPos.xCoord + mc.thePlayer.x - hand[0] + oset[0],
//					-UIPos.yCoord + mc.thePlayer.bb.minY + hand[1],
//					-UIPos.zCoord + mc.thePlayer.z - hand[2] + oset[1],
//					1, 0
//			);
//			Tessellator.instance.draw();
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		} else {
			data.better_than_vr$mouseOverride()[0] = Double.NaN;
			data.better_than_vr$mouseOverride()[1] = Double.NaN;
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		Lighting.enableLight();
	}
}
