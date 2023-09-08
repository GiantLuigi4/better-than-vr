package tfc.btvr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Cube;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.client.render.model.ModelPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.lwjgl3.openvr.Eye;
import tfc.btvr.mixin.model.LivingRendererAccessor;

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
		{
			GL11.glMatrixMode(GL11.GL_PROJECTION_MATRIX);
			
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
			GL11.glScaled(1, 1, -1);
		}
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
		
		Device head = Device.HEAD;
		HmdMatrix34 matr = head.getMatrix();
		
		data = toArray(matr);
		buffer.put(data);
		buffer.flip();
		
		GL11.glLoadMatrix(buffer);
		GL11.glTranslated(matr.m(3) * -1, -matr.m(7), matr.m(11) * -1);
		GL11.glTranslated(0, mc.thePlayer.heightOffset, 0);
	}
	
	protected static void draw(Cube cube) {
		float rpX = cube.rotationPointX;
		float rpY = cube.rotationPointY;
		float rpZ = cube.rotationPointZ;
		float rX = cube.rotateAngleX;
		float rY = cube.rotateAngleY;
		float rZ = cube.rotateAngleZ;
		
		cube.setRotationPoint(0, 2, 0);
		cube.setRotationAngle(0, 0, 0);
		boolean show = cube.showModel;
		
		cube.showModel = true;
		cube.render(0.0625F);
		
		cube.showModel = show;
		cube.setRotationPoint(rpX, rpY, rpZ);
		cube.setRotationAngle(rX, rY, rZ);
	}
	
	public static void renderPlayer(EntityPlayerSP thePlayer, float renderPartialTicks, RenderGlobal renderGlobal) {
		Device rightHand = Device.getDeviceForRole(DeviceType.RIGHT_HAND);
		Device leftHand = Device.getDeviceForRole(DeviceType.LEFT_HAND);
		
		
		EntityRenderDispatcher dispatcher = EntityRenderDispatcher.instance;
		if (dispatcher.renderEngine == null) return;
		
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		ICamera camera = mc.activeCamera;
		
		PlayerRenderer renderer = (PlayerRenderer) (LivingRenderer) dispatcher.getRenderer(thePlayer);
		ModelBase modelBase = ((LivingRendererAccessor) renderer).getMainModel();
		ModelPlayer mdl = (ModelPlayer) modelBase;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glColorMask(true, true, true, true);
		GL11.glColor4f(1, 1, 1, 1f);
		
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
			
			GL11.glTranslated(0, -mc.thePlayer.heightOffset + mc.thePlayer.getHeadHeight(), 0);
			renderer.loadEntityTexture(thePlayer);
			
			GL11.glTranslated(-camera.getX(), -camera.getY(), -camera.getZ());
			GL11.glTranslated(thePlayer.x, thePlayer.y, thePlayer.z);
			GL11.glMultMatrix(buffer);
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glScaled(-1, 1, 1);
			
			GL11.glScaled(0.5, 0.5, 0.5);
			draw(mdl.bipedRightArm);
			draw(mdl.bipedRightArmOverlay);
			
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
			
			GL11.glTranslated(0, -mc.thePlayer.heightOffset + mc.thePlayer.getHeadHeight(), 0);
			renderer.loadEntityTexture(thePlayer);
			
			GL11.glTranslated(-camera.getX(), -camera.getY(), -camera.getZ());
			GL11.glTranslated(thePlayer.x, thePlayer.y, thePlayer.z);
			GL11.glMultMatrix(buffer);
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glScaled(-1, 1, 1);
			
			GL11.glScaled(0.5, 0.5, 0.5);
			draw(mdl.bipedLeftArm);
			draw(mdl.bipedLeftArmOverlay);
			
			GL11.glPopMatrix();
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
