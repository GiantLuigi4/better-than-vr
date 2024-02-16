package tfc.btvr.lwjgl3.oculus.ovr;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import tfc.btvr.lwjgl3.generic.Eye;

import java.nio.ByteBuffer;

public class OVREye extends Eye {
	int fboId;
	int rboId;
	int texId;
	
	public void close() {
		GL11.glDeleteTextures(texId);
		GL30.glDeleteFramebuffers(fboId);
		GL30.glDeleteRenderbuffers(rboId);
	}
	
	public OVREye(int id, int w, int h) {
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
	}
	
	@Override
	public int fboId() {
		return fboId;
	}
	
	@Override
	public void submit() {
//		OVRCompositor.submit(id, fboId);
	}
	
	@Override
	public void delete() {
		throw new RuntimeException("NYI");
	}
}
