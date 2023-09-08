package tfc.btvr.lwjgl3.openvr;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// https://github.com/ValveSoftware/openvr/blob/5e45960cf44d6eb19bbadcae4a3d32578a380c17/samples/hellovr_opengl/hellovr_opengl_main.cpp#L1379-L1414
public class Eye {
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
	
	public Eye(IntBuffer w, IntBuffer h) {
		fboId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		
		texId = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 0);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w.get(0), h.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		rboId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, w.get(0), h.get(0));
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rboId);
		
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("YAY!");
			GL11.glViewport(0, 0, w.get(0), h.get(0));
			GL11.glClearColor(1, 1, 1, 1f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		}
		
		texture = Texture.create();
		texture.set(texId, VR.ETextureType_TextureType_OpenGL, VR.EColorSpace_ColorSpace_Gamma);
	}
	
	public void submit(int eye) {
		int ret = VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Left + eye, texture, null, VR.EVRSubmitFlags_Submit_Default);
		if (ret != 0) System.err.println((eye == 1) ? "Right" : "Left" + " eye failed to submit: " + ret);
	}
}
