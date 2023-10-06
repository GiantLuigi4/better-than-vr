package tfc.btvr.lwjgl3.oculus.ovr;

import org.lwjgl.ovr.OVR;
import org.lwjgl.ovr.OVRFovPort;
import org.lwjgl.ovr.OVRSizei;

import java.nio.IntBuffer;

public class OVRCompositor {
	
	public static void checkEyeSize(IntBuffer w, IntBuffer h) {
		// https://github.com/Gasteclair/LWJGL-libOVR-VR-in-JAVA-/blob/988762bc157803a0a092773225e021ff507c9fa1/OVRRenderer.java#L61-L68
		OVRFovPort port = OVRFovPort.malloc();
		port.set(
				1.43f,
				1.43f,
				1.43f,
				1.43f
		);
		OVRSizei sizei0 = OVRSizei.calloc();
		OVRSizei sizei1 = OVRSizei.calloc();
		
		OVR.ovr_GetFovTextureSize(
				OVRSession.session.get(0),
				OVR.ovrEye_Left, port,
				1, sizei0
		);
		OVR.ovr_GetFovTextureSize(
				OVRSession.session.get(0),
				OVR.ovrEye_Right, port,
				1, sizei1
		);
		
		w.put(0, sizei0.w() + sizei1.w());
		h.put(1, sizei0.h() + sizei1.h());
	}
}
