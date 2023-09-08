package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

// https://github.com/ValveSoftware/openvr/blob/5e45960cf44d6eb19bbadcae4a3d32578a380c17/samples/hellovr_opengl/hellovr_opengl_main.cpp#L1379-L1414
public class Eye {
	protected static Eye activeEye = null;
	
	public static Eye getActiveEye() {
		return activeEye;
	}
	
	Texture texture;
	int fboId;
	int rboId;
	int texId;
	
	public final int width;
	public final int height;
	public final int id;
	
	public void close() {
		GL11.glDeleteTextures(texId);
		GL30.glDeleteFramebuffers(fboId);
		GL30.glDeleteRenderbuffers(rboId);
		texture.close();
	}
	
	public Eye(int id, IntBuffer w, IntBuffer h) {
		this.id = id;
		width = w.get(0);
		height = h.get(0);
		
		fboId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		
		texId = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w.get(0), h.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		rboId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, w.get(0), h.get(0));
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rboId);
		
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE) {
			GL11.glViewport(0, 0, w.get(0), h.get(0));
			GL11.glClearColor(1, 1, 1, 1f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		}
		
		texture = Texture.create();
		texture.set(texId, VR.ETextureType_TextureType_OpenGL, VR.EColorSpace_ColorSpace_Gamma);
	}
	
	private static final HashMap<Integer, String> ERRS = new HashMap<>();
	
	static {
		for (Field field : VR.class.getFields()) {
			if (field.getName().startsWith("EVRCompositorError")) {
				try {
					ERRS.put((Integer) field.get(null), field.getName().substring("EVRCompositorError_VRCompositorError_".length()));
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		}
	}
	
	public void submit() {
		int ret = VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Left + id, texture, null, VR.EVRSubmitFlags_Submit_Default);
		if (ret != 0) {
			String name = ERRS.getOrDefault(ret, "null");
			System.err.println((id == 1) ? "Right" : "Left" + " eye failed to submit: " + name + " (" + ret + ")");
		}
	}
	
	public void activate() {
		activeEye = this;
	}
	
	public static void deactivate() {
		activeEye = null;
	}
	
	public int fboId() {
		return fboId;
	}
	
	private static final HmdMatrix44 projection = HmdMatrix44.calloc();
	
	public static HmdMatrix44 getProjectionMatrix(int eye, float zNear, float zFar) {
		return VRSystem.VRSystem_GetProjectionMatrix(eye, zNear, zFar, projection);
	}
}
