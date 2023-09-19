package tfc.btvr.lwjgl3;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRInput;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.IntBuffer;

public class BTVRSetup {
	public static final int token;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::init");
	
	static {
//		String pth = System.getProperty("org.lwjgl.librarypath");
		LOGGER.info("Load VR");
		Library.initialize();
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
		
//		System.setProperty("org.lwjgl.librarypath", pth);
//		Runtime.getRuntime().addShutdownHook(new Thread(VR::VR_ShutdownInternal));
	}
	
	public static boolean checkVR() {
		return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
	}
}
