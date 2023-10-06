package tfc.btvr.lwjgl3.oculus.ovr;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.ovr.OVR;
import org.lwjgl.ovr.OVRGraphicsLuid;
import org.lwjgl.ovr.OVRTrackingState;

public class OVRSession {
	public static final PointerBuffer session;
	public static final OVRGraphicsLuid luid;
	protected static final OVRTrackingState trackingState;
	
	static {
		int result = OVR.ovr_Initialize(null);
		if (result <= 0) throw new RuntimeException("Runtime is not installed");
		session = BufferUtils.createPointerBuffer(1);
		luid = OVRGraphicsLuid.create();
		
		if (OVR.ovr_Create(session, luid) != 0) {
			System.err.println("Couldn't create OVR!");
			System.exit(-1);
		}
		
		trackingState = OVRTrackingState.malloc();
	}
	
	public static void end() {
		if (session != null) {
			OVR.ovr_Destroy(session.get(0));
			OVR.ovr_Shutdown();
		}
	}
}
