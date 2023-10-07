package tfc.btvr.lwjgl3;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRInput;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.ovr.OVRDetectResult;
import org.lwjgl.ovr.OVRUtil;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfc.btvr.lwjgl3.oculus.ovr.OVRSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.IntBuffer;

public class BTVRSetup {
	public static final int token;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::init");
	
	static {
		LOGGER.info("Load VR");
		Library.initialize();
		
		if (VRManager.getActiveMode() == VRMode.STEAM_VR) {
			LOGGER.info("Steam VR Selected");
			
			VR.getLibrary(); // load the library, just to not mess up LWJGL2
			if (checkVR()) {
				MemoryStack stack = MemoryStack.stackPush();
				IntBuffer peError = stack.mallocInt(1);
				token = VR.VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene);
				OpenVR.create(token);
			} else {
				token = 0;
			}
			LOGGER.info("VR Loaded, token = " + token);
			
			LOGGER.info("Writing actions");
			try {
				File fl = new File(FabricLoader.getInstance().getGameDir() + "/vr/actions.json");
				if (!fl.exists()) {
					InputStream is = BTVRSetup.class.getClassLoader().getResourceAsStream("btvr/actions.json");
					fl.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(fl);
					fos.write(is.readAllBytes());
					fos.flush();
					fos.close();
					is.close();
				}
			} catch (Throwable err) {
				throw new RuntimeException(err);
			}
			try {
				File fl = new File(FabricLoader.getInstance().getGameDir() + "/vr/bindings/oculus_touch.json");
				if (!fl.exists()) {
					InputStream is = BTVRSetup.class.getClassLoader().getResourceAsStream("btvr/bindings/oculus_touch.json");
					fl.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(fl);
					fos.write(is.readAllBytes());
					fos.flush();
					fos.close();
					is.close();
				}
			} catch (Throwable err) {
				throw new RuntimeException(err);
			}
			
			LOGGER.info("Loading actions");
			
			int err = VRInput.VRInput_SetActionManifestPath(FabricLoader.getInstance().getGameDir().toAbsolutePath() + "/vr/actions.json");
			if (err != 0) {
				System.out.println("Actions setup with error: " + err);
			}
		} else if (VRManager.getActiveMode() == VRMode.OCULUS_VR) {
			LOGGER.info("Oculus VR Selected");
			OVRSession.init();
			token = -1;
		} else {
			token = -1;
		}
	}
	
	public static VRMode getDefaultMode() {
		if (checkSteamVR())
			return VRMode.STEAM_VR;
		else if (checkOculusVR())
			return VRMode.OCULUS_VR;
		
		return VRMode.NONE;
	}
	
	protected static boolean checkSteamVR() {
		return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
	}
	
	protected static boolean checkOculusVR() {
		OVRDetectResult result = OVRDetectResult.calloc();
		OVRUtil.ovr_Detect(0, result);
		return result.IsOculusServiceRunning();
	}
	
	public static boolean checkVR() {
		return checkSteamVR() || checkOculusVR();
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
	
	public static void whenTheGameHasBeenRequestedToShutdownIShouldAlsoShutdownTheSteamVRAndOVRLogic() {
		switch (VRManager.getActiveMode()) {
			case STEAM_VR:
				VR.VR_ShutdownInternal();
				break;
			case OCULUS_VR:
				OVRSession.end();
				break;
		}
	}
}
