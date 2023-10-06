package tfc.btvr.lwjgl3.generic;

import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.VRSystem;
import tfc.btvr.lwjgl3.VRManager;

public abstract class Eye {
	protected static Eye activeEye = null;
	
	public final int id, width, height;
	
	public static Eye getActiveEye() {
		return activeEye;
	}
	
	public void activate() {
		activeEye = this;
	}
	
	public static void deactivate() {
		activeEye = null;
	}
	
	public Eye(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public abstract void submit();
	
	public abstract int fboId();
	
	// steamvr
	
	private static final HmdMatrix44 svrProjection = HmdMatrix44.calloc();
	private static final HmdMatrix34 svrTranslation = HmdMatrix34.calloc();
	
	public static HmdMatrix44 getProjectionMatrix(int eye, float zNear, float zFar) {
		switch (VRManager.getActiveMode()) {
			case OCULUS_VR:
				break;
			case STEAM_VR:
				VRSystem.VRSystem_GetProjectionMatrix(eye, zNear, zFar, svrProjection);
				break;
			default:
				throw new RuntimeException("Cannot get a VR matrix for a non-vr mode");
		}
		return svrProjection;
	}
	
	public static HmdMatrix34 getTranslationMatrix(int id) {
		switch (VRManager.getActiveMode()) {
			case OCULUS_VR:
				break;
			case STEAM_VR:
				VRSystem.VRSystem_GetEyeToHeadTransform(id, svrTranslation);
				break;
			default:
				throw new RuntimeException("Cannot get a VR matrix for a non-vr mode");
		}
		return svrTranslation;
	}
}
