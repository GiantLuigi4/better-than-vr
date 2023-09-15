package tfc.btvr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Cube;
import net.minecraft.core.HitResult;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.lwjgl3.openvr.Eye;
import tfc.btvr.math.VecMath;

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
		
		Device head = Device.HEAD;
		
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
		
		HmdMatrix34 matr = head.getMatrix();
		
		data = toArray(matr);
		buffer.put(data);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
		GL11.glTranslated(matr.m(3) * -1, -matr.m(7), matr.m(11) * -1);
		if (instance != null && mc.thePlayer != null) {
			GL11.glTranslated(0, mc.thePlayer.heightOffset, 0);
			GL11.glTranslated(
					instance.getX(pct) - mc.thePlayer.x,
					instance.getY(pct) - mc.thePlayer.y,
					instance.getZ(pct) - mc.thePlayer.z
			);
		}
	}
	
	private static Cube leftArm = new Cube(40, 16, 64, 64);
	private static Cube rightArm = new Cube(32, 48, 64, 64);
	
	static {
		leftArm.mirror = true;
		leftArm.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0, true);
		
		rightArm.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 4, 0, true);
	}
	
	protected static void draw(Cube cube) {
		float rpX = cube.rotationPointX;
		float rpY = cube.rotationPointY;
		float rpZ = cube.rotationPointZ;
		float rX = cube.rotateAngleX;
		float rY = cube.rotateAngleY;
		float rZ = cube.rotateAngleZ;
		
		cube.setRotationPoint(0, -6, 0);
		cube.setRotationAngle(0, 0, 0);
		boolean show = cube.showModel;
		
		cube.showModel = true;
		cube.render(1);
		
		cube.showModel = show;
		cube.setRotationPoint(rpX, rpY, rpZ);
		cube.setRotationAngle(rX, rY, rZ);
	}
	
	public static void renderPlayer(EntityPlayer thePlayer, float renderPartialTicks, RenderGlobal renderGlobal) {
		renderPlayer(false, thePlayer, renderPartialTicks, renderGlobal);
	}
	
	public static void renderPlayer(boolean menu, EntityPlayer thePlayer, float renderPartialTicks, RenderGlobal renderGlobal) {
		Device rightHand = Device.getDeviceForRole(DeviceType.RIGHT_HAND);
		Device leftHand = Device.getDeviceForRole(DeviceType.LEFT_HAND);
		
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		ICamera camera = mc.activeCamera;
		
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
		
		double scl = 1 / 32d;
		
		{
			GL11.glPushMatrix();
			HmdMatrix34 matr = rightHand.getMatrix();
			float[] data = new float[]{
					matr.m(0), matr.m(4), matr.m(8), 0,
					matr.m(1), matr.m(5), matr.m(9), 0,
					matr.m(2), matr.m(6), matr.m(10), 0,
					matr.m(3), matr.m(7), matr.m(11), 1,
			};
			buffer.put(data);
			buffer.flip();
			
			if (!menu && camera != null) {
				GL11.glTranslated(0, -mc.thePlayer.heightOffset + mc.thePlayer.getHeadHeight(), 0);
				
				GL11.glTranslated(-camera.getX(renderPartialTicks), -camera.getY(renderPartialTicks), -camera.getZ(renderPartialTicks));
				GL11.glTranslated(thePlayer.x, thePlayer.y, thePlayer.z);
			}
			GL11.glMultMatrix(buffer);
			GL11.glScaled(-1, -1, -1);
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glRotatef(180, 0, 1, 0);

//			GL11.glTranslated(0, -2/8d, 0);
			
			GL11.glScaled(scl, scl, scl);
			draw(rightArm);
//			draw(mdl.bipedRightArmOverlay);
			
			GL11.glPopMatrix();
		}
		{
			GL11.glPushMatrix();
			HmdMatrix34 matr = leftHand.getMatrix();
			float[] data = new float[]{
					matr.m(0), matr.m(4), matr.m(8), 0,
					matr.m(1), matr.m(5), matr.m(9), 0,
					matr.m(2), matr.m(6), matr.m(10), 0,
					matr.m(3), matr.m(7), matr.m(11), 1,
			};
			buffer.put(data);
			buffer.flip();
			
			if (!menu && camera != null) {
				GL11.glTranslated(0, -mc.thePlayer.heightOffset + mc.thePlayer.getHeadHeight(), 0);
				
				GL11.glTranslated(-camera.getX(renderPartialTicks), -camera.getY(renderPartialTicks), -camera.getZ(renderPartialTicks));
				GL11.glTranslated(thePlayer.x, thePlayer.y, thePlayer.z);
			}
			GL11.glMultMatrix(buffer);
			GL11.glScaled(1, -1, -1);
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glRotatef(180, 0, 1, 0);

//			GL11.glTranslated(0, -2/8d, 0);
			
			GL11.glScaled(scl, scl, scl);
			draw(leftArm);
//			draw(mdl.bipedLeftArmOverlay);
			
			GL11.glPopMatrix();
		}
		
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
