package tfc.btvr.lwjgl3;

import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.ovr.OVR;
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
		
		String pth = System.getProperty("org.lwjgl.librarypath");
		Library.initialize();
		
		// load natives
		// done here because doing it while the game's running may lead to problems
		if (OSInfo.supportsSteamVR()) VR.getLibrary();
		if (OSInfo.supportsOVR()) OVR.ovr_GetVersionString();
		
		Config.init();
		
		if (VRManager.getActiveMode() == VRMode.STEAM_VR) {
			LOGGER.info("Steam VR Selected");
			OpenVRSession.setup();
		} else if (VRManager.getActiveMode() == VRMode.OCULUS_VR) {
			LOGGER.info("Oculus VR Selected");
			OVRSession.setup();
		}
		if (pth != null)
			System.setProperty("org.lwjgl.librarypath", pth);
		else LOGGER.info("org.lwjgl.librarypath was null");
	}
	
	public static VRMode getDefaultMode() {
		if (checkSteamVR())
			return VRMode.STEAM_VR;
		else if (checkOculusVR())
			return VRMode.OCULUS_VR;
		
		return VRMode.STEAM_VR;
	}
	
	static class OSInfo {
		public static final String os = System.getProperty("os.name").toLowerCase();
		public static final String arch = System.getProperty("os.arch");
		
		// os detection
		// copied from org.gradle.internal.os.OperatingSystem
		public static final boolean windows = os.contains("windows");
		public static final boolean linux = os.contains("linux");
		public static final boolean mac = os.contains("mac os x") || os.contains("darwin") || os.contains("osx");
		
		// arch detection
		// recreated from lwjgl's generated build script
		// https://www.lwjgl.org/customize
		public static final boolean x86 = arch.contains("64") && !arch.startsWith("aarch64");
		public static final boolean arm64 =
				(windows && arch.contains("64") && arch.startsWith("aarch64")) ||
						(linux && ((arch.startsWith("arm") || arch.startsWith("aarch64")) && (arch.startsWith("64") || arch.startsWith("armv8"))));
		public static final boolean arm32 = (linux && ((arch.startsWith("arm") || arch.startsWith("aarch64")) && !(arch.startsWith("64") || arch.startsWith("armv8"))));
		public static final boolean riscv = arch.startsWith("riscv");
		public static final boolean ppc = arch.startsWith("ppc");
		
		public static boolean supportsSteamVR() {
			return (OSInfo.windows && (OSInfo.x86 || OSInfo.arm64)) ||
					(OSInfo.linux && OSInfo.arm64) ||
					(OSInfo.mac && OSInfo.arm64);
		}
		
		public static boolean supportsOVR() {
			return OSInfo.windows && (OSInfo.x86 && OSInfo.arm64);
		}
	}
	
	protected static boolean checkSteamVR() {
		if (OSInfo.supportsSteamVR()) {
			return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
		} else {
			LOGGER.warn("Platform does not support OpenVR (SteamVR)");
		}
		
		return false;
	}
	
	protected static boolean checkOculusVR() {
//		if (OSInfo.supportsOVR()) {
//			OVRDetectResult result = OVRDetectResult.calloc();
//			OVRUtil.ovr_Detect(0, result);
//			return result.IsOculusServiceRunning();
//		} else {
//			LOGGER.warn("Platform does not support OVR (Oculus VR)");
//		}
		
		return false;
	}
	
	public static boolean checkVR() {
		return VRManager.getActiveMode() != VRMode.NONE;
	}
	
	public static void getSize(IntBuffer w, IntBuffer h) {
		if (VRManager.getActiveMode() == VRMode.STEAM_VR) {
			VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);
		} else if (VRManager.getActiveMode() == VRMode.OCULUS_VR) {
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
