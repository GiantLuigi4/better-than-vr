package tfc.btvr.lwjgl3.oculus.ovr;

import org.lwjgl.PointerBuffer;
import org.lwjgl.ovr.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OVRSession {
	private static PointerBuffer session;
	private static OVRGraphicsLuid luid;
	private static OVRTrackingState trackingState;
	private static OVRLogCallback callback;
	private static OVRInitParams params;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::init::ovr");
	
	public static void setup() {
		params = OVRInitParams.calloc()
				.LogCallback(callback = new OVRLogCallback() {
					@Override
					public void invoke(long userData, int level, long message) {
						LOGGER.debug("LibOVR [" + level + "] " + MemoryUtil.memASCII(message));
					}
				})
				.Flags(OVR.ovrInit_Debug | OVR.ovrInit_FocusAware);
		
		if (OVR.ovr_Initialize(params) != OVRErrorCode.ovrSuccess) {
			OVRErrorInfo info = OVRErrorInfo.malloc();
			OVR.ovr_GetLastErrorInfo(info);
			LOGGER.error(info.ErrorStringString());
			info.free();
			LOGGER.error("Failed to initialize OVR");
			System.exit(-1);
		}
		
		session = MemoryUtil.memAllocPointer(1);
		luid = OVRGraphicsLuid.create();
		
		if (OVR.ovr_Create(session, luid) != 0) {
			OVRErrorInfo info = OVRErrorInfo.malloc();
			OVR.ovr_GetLastErrorInfo(info);
			LOGGER.error(info.ErrorStringString());
			info.free();
			
			LOGGER.error("Couldn't create OVR!");
			System.exit(-1);
		}
		
		trackingState = OVRTrackingState.malloc();
	}
	
	public static void end() {
		if (session != null) {
			OVR.ovr_Destroy(session.get(0));
			OVR.ovr_Shutdown();
			
			trackingState.free();
			luid.free();
			session.free();
			
			if (callback != null) callback.free();
			if (params != null) params.free();
		}
	}
	
	public static PointerBuffer getSession() {
		return session;
	}
}
