package tfc.btvr.lwjgl3;

import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class BTVRSetup {
	public static final int token;
	
	static {
		String pth = System.getProperty("org.lwjgl.librarypath");
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

		System.setProperty("org.lwjgl.librarypath", pth);

		Runtime.getRuntime().addShutdownHook(new Thread(VR::VR_ShutdownInternal));
	}
	
	public static boolean checkVR() {
		return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
	}
}
