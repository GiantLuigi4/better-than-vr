package tfc.btvr.lwjgl3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import tfc.btvr.lwjgl3.openvr.Eye;

import java.nio.IntBuffer;

public class VRRenderManager {
	private static Eye leftEye;
	private static Eye rightEye;
	
	
	public static void init(IntBuffer w, IntBuffer h) {
		leftEye = new Eye(w, h);
		rightEye = new Eye(w, h);

//		frameFinished();
	}
	
	public static void frameFinished() {
		VRManager.tick();
		
		leftEye.submit(0);
		rightEye.submit(1);
		VRCompositor.VRCompositor_PostPresentHandoff();
	}
}
