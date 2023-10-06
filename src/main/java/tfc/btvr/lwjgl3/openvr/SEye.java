package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import tfc.btvr.lwjgl3.generic.Eye;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;

// https://github.com/ValveSoftware/openvr/blob/5e45960cf44d6eb19bbadcae4a3d32578a380c17/samples/hellovr_opengl/hellovr_opengl_main.cpp#L1379-L1414
public class SEye extends Eye {
	Texture texture;
	int fboId;
	int rboId;
	int texId;
	
	public void close() {
		GL11.glDeleteTextures(texId);
		GL30.glDeleteFramebuffers(fboId);
		GL30.glDeleteRenderbuffers(rboId);
		texture.close();
	}
	
	public SEye(int id, int w, int h) {
		super(id, w, h);
		
		fboId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		
		texId = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		rboId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, w, h);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rboId);
		
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE) {
			GL11.glViewport(0, 0, w, h);
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
	
	public int fboId() {
		return fboId;
	}
}
