package tfc.btvr.lwjgl3;

import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfc.btvr.Config;
import tfc.btvr.lwjgl3.oculus.ovr.OVRSession;
import tfc.btvr.lwjgl3.openvr.OpenVRSession;

import java.nio.IntBuffer;

public class BTVRSetup {
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::init");
	
	static {
		LOGGER.info("Load VR");
		Library.initialize();
		
		Config.init();

		if (VRManager.getActiveMode() == VRMode.STEAM_VR) {
			LOGGER.info("Steam VR Selected");
			OpenVRSession.setup();
		} else if (VRManager.getActiveMode() == VRMode.OCULUS_VR) {
			LOGGER.info("Oculus VR Selected");
			OVRSession.setup();
		}
	}
	
	public static VRMode getDefaultMode() {
		if (checkSteamVR())
			return VRMode.STEAM_VR;
		else if (checkOculusVR())
			return VRMode.OCULUS_VR;
		
		return VRMode.STEAM_VR;
	}
	
	protected static boolean checkSteamVR() {
		return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
//		return false;
	}
	
	protected static boolean checkOculusVR() {
		return false;
//		OVRDetectResult result = OVRDetectResult.calloc();
//		OVRUtil.ovr_Detect(0, result);
//		return result.IsOculusServiceRunning();
	}
	
	public static boolean checkVR() {
		return VRManager.getActiveMode() != VRMode.NONE;
	}
	
	public static void getSize(IntBuffer w, IntBuffer h) {
		if (checkSteamVR()) {
			VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);
		} else if (checkOculusVR()) {
			// eye resolution can vary from eye to eye with OVR
			// not worth checking here
			w.put(0, 0);
			h.put(0, 0);
		}
	}
	
	public static void whenTheGameHasBeenRequestedToShutdownIShouldAlsoShutdownTheSteamVRAndOVRLogicToAvoidCreatingProblemsAndDeadlocksLol(VRMode closing) {
		switch (closing) {
			case STEAM_VR:
				OpenVRSession.end();
				break;
			case OCULUS_VR:
				OVRSession.end();
				break;
		}
	}
}
