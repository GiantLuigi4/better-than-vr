package tfc.btvr.lwjgl3;

import net.minecraft.client.GameResolution;
import net.minecraft.client.render.Framebuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.Texture;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.VRCompositor;
import tfc.btvr.lwjgl3.openvr.Eye;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VRRenderManager {
	private static Eye leftEye;
	private static Eye rightEye;
	
	
	public static void init(IntBuffer w, IntBuffer h) {
		leftEye = new Eye(0, w, h);
		rightEye = new Eye(1, w, h);
	}
	
	public static void frameFinished(boolean reducedWork, boolean left) {
		if (VRCompositor.VRCompositor_CanRenderScene()) {
//			if (!reducedWork || left)
			leftEye.submit();
//			if (!reducedWork || !left)
			rightEye.submit();
			VRCompositor.VRCompositor_PostPresentHandoff();
		}
	}
	
	public static void start(int eye) {
		if (eye == 0) leftEye.activate();
		else if (eye == 1) rightEye.activate();
		else Eye.deactivate();
	}
	
	private static int fbWidth = 0;
	private static int fbHeight = 0;
	private static int renderWidth = 0;
	private static int renderHeight = 0;
	
	private static final Framebuffer UIFbo = new Framebuffer();
	private static final net.minecraft.client.render.Texture UITex = new Texture();
	private static final net.minecraft.client.render.Texture UIDep = new Texture();
	private static final Framebuffer overlayFbo = new Framebuffer();
	private static final net.minecraft.client.render.Texture overlayTex = new Texture();
	private static final net.minecraft.client.render.Texture overlayDep = new Texture();
	
	public static void startFrame(GameResolution resolution, double renderScale, boolean useLinearFiltering) {
		int scaledWidth = (int) (renderScale * (double) resolution.width);
		int scaledHeight = (int) (renderScale * (double) resolution.height);
		if (fbWidth != resolution.width || fbHeight != resolution.height || renderWidth != scaledWidth || renderHeight != scaledHeight) {
			if (!UITex.isGenerated()) UITex.generate();
			if (!UIDep.isGenerated()) UIDep.generate();
			if (!overlayTex.isGenerated()) overlayTex.generate();
			if (!overlayDep.isGenerated()) overlayDep.generate();
			
			fbWidth = resolution.width;
			fbHeight = resolution.height;
			renderWidth = scaledWidth;
			renderHeight = scaledHeight;
			int filterMode = useLinearFiltering ? 9729 : 9728;
			
			UIFbo.generate();
			UIFbo.bind();
			UITex.bind();
			GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 6408, 5121, (ByteBuffer) null);
			GL11.glTexParameteri(3553, 10241, filterMode);
			GL11.glTexParameteri(3553, 10240, filterMode);
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
			ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, UITex.id(), 0);
			UIDep.bind();
			GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5121, (ByteBuffer) null);
			GL11.glTexParameteri(3553, 10241, filterMode);
			GL11.glTexParameteri(3553, 10240, filterMode);
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
			ARBFramebufferObject.glFramebufferTexture2D(36160, 36096, 3553, UIDep.id(), 0);
			
			overlayFbo.generate();
			overlayFbo.bind();
			overlayTex.bind();
			GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 6408, 5121, (ByteBuffer) null);
			GL11.glTexParameteri(3553, 10241, filterMode);
			GL11.glTexParameteri(3553, 10240, filterMode);
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
			ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, overlayTex.id(), 0);
			overlayDep.bind();
			GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5121, (ByteBuffer) null);
			GL11.glTexParameteri(3553, 10241, filterMode);
			GL11.glTexParameteri(3553, 10240, filterMode);
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
			ARBFramebufferObject.glFramebufferTexture2D(36160, 36096, 3553, overlayDep.id(), 0);
		}
		
//		UIFbo.bind();
//		GL11.glClearColor(0, 0, 0, 0f);
//		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
//		overlayFbo.bind();
//		GL11.glClearColor(0, 0, 0, 0f);
//		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
	}
	
	private static boolean grabbingUI = false;
	private static boolean grabbingOverlay = false;
	
	public static void grabUI() {
		if (grabbingOverlay)
			overlayFbo.bind();
		else UIFbo.bind();
		
		GL11.glClearColor(0, 0, 0, 0f);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public static void grabUI(boolean overlay) {
		GL11.glColorMask(true, true, true, true);
		grabbingUI = true;
		grabbingOverlay = overlay;
		grabUI();
	}
	
	public static void releaseUI() {
		if (grabbingUI) {
			grabbingUI = false;
			if (grabbingOverlay)
				overlayFbo.unbind();
			else UIFbo.unbind();
		}
	}
	
	public static boolean isGrabbingUI() {
		return grabbingUI;
	}
	
	public static void bindGUI() {
		UITex.bind();
	}
	
	public static void blitUI() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		
		overlayTex.bind();
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-1, -1, 0, 0.0, 0.0);
		tessellator.addVertexWithUV(-1, 1, 0, 0.0, 1.0);
		tessellator.addVertexWithUV(1, 1, 0, 1.0, 1.0);
		tessellator.addVertexWithUV(1, -1, 0, 1.0, 0.0);
		tessellator.draw();
		
		UITex.bind();
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-1, -1, 0, 0.0, 0.0);
		tessellator.addVertexWithUV(-1, 1, 0, 0.0, 1.0);
		tessellator.addVertexWithUV(1, 1, 0, 1.0, 1.0);
		tessellator.addVertexWithUV(1, -1, 0, 1.0, 0.0);
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}
