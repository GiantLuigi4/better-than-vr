package tfc.btvr.lwjgl3;

import net.minecraft.client.GameResolution;
import net.minecraft.client.render.Framebuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.Texture;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.ovr.OVR;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.oculus.ovr.OVRCompositor;
import tfc.btvr.lwjgl3.oculus.ovr.OVREye;
import tfc.btvr.lwjgl3.openvr.SEye;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VRRenderManager {
	private static Eye leftEye;
	private static Eye rightEye;
	
	
	public static void init(IntBuffer w, IntBuffer h) {
		switch (VRManager.getActiveMode()) {
			case STEAM_VR:
				leftEye = new SEye(0, w.get(0), h.get(0));
				rightEye = new SEye(1, w.get(0), h.get(0));
				break;
			case OCULUS_VR:
				OVRCompositor.checkEyeSize(OVR.ovrEye_Left, w, h);
				leftEye = new OVREye(0, w.get(0), h.get(0));
				OVRCompositor.checkEyeSize(OVR.ovrEye_Right, w, h);
				rightEye = new OVREye(1, w.get(0), h.get(0));
				break;
		}
	}
	
	public static void frameFinished(boolean reducedWork, boolean left) {
		if (VRCompositor.VRCompositor_CanRenderScene()) {
			leftEye.submit();
			rightEye.submit();
			VRCompositor.VRCompositor_PostPresentHandoff();
//			VRCompositor.VRCompositor_ClearLastSubmittedFrame();
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
	
	private static double pct;
	
	public static double getPct() {
		return pct;
	}
	
	public static void startFrame(GameResolution resolution, double renderScale, boolean useLinearFiltering, double pct) {
		VRRenderManager.pct = pct;
		
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
			int overlayWidth = 960;
			GL11.glTexImage2D(3553, 0, 6408, overlayWidth, overlayWidth / 2, 0, 6408, 5121, (ByteBuffer) null);
			GL11.glTexParameteri(3553, 10241, GL11.GL_NEAREST);
			GL11.glTexParameteri(3553, 10240, GL11.GL_NEAREST);
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
			ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, overlayTex.id(), 0);
			overlayDep.bind();
			GL11.glTexImage2D(3553, 0, 6402, overlayWidth, overlayWidth / 2, 0, 6402, 5121, (ByteBuffer) null);
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
	}
	
	public static void grabUI(boolean overlay) {
		GL11.glColorMask(true, true, true, true);
		grabbingUI = true;
		grabbingOverlay = overlay;
		grabUI();
		
		GL11.glClearColor(0, 0, 0, 0f);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
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
	
	public static void bindUI() {
		overlayTex.bind();
	}
	
	public static void blitUI() {
		if (!Config.HYBRID_MODE.get())
			return;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		
//		overlayTex.bind();
//		tessellator.startDrawingQuads();
//		tessellator.addVertexWithUV(-1, -1, 0, 0.0, 0.0);
//		tessellator.addVertexWithUV(-1, 1, 0, 0.0, 1.0);
//		tessellator.addVertexWithUV(1, 1, 0, 1.0, 1.0);
//		tessellator.addVertexWithUV(1, -1, 0, 1.0, 0.0);
//		tessellator.draw();
		
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
