package tfc.btvr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.camera.ICamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.Eye;

import java.nio.FloatBuffer;

public class VRCamera {
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
	
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
					matr.m(0), matr.m(1), matr.m(2), 0,
					matr.m(4), matr.m(5), matr.m(6), 0,
					matr.m(8), matr.m(9), matr.m(10), 0,
					0, 0, 0, 1
			};
			buffer.put(data);
			buffer.flip();
			GL11.glLoadMatrix(buffer);
			GL11.glScaled(1, 1, -1);
		}
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
		
		Device head = Device.HEAD;
		HmdMatrix34 matr = head.getMatrix();
		
		data = new float[]{
				matr.m(0), matr.m(1), matr.m(2), 0,
				matr.m(4), matr.m(5), matr.m(6), 0,
				matr.m(8), matr.m(9), matr.m(10), 0,
				0, 0, 0, 1,
		};
		buffer.put(data);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
		GL11.glTranslated(matr.m(3) * -1, -matr.m(7), matr.m(11) * -1);
		GL11.glTranslated(0, instance.getY() - mc.thePlayer.bb.minY, 0);
	}
}
