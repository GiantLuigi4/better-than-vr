package tfc.btvr.lwjgl3.oculus.ovr;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.ovr.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OVRSession {
	public static final PointerBuffer session;
	public static final OVRGraphicsLuid luid;
	protected static final OVRTrackingState trackingState;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::ovr");
	
	private static final OVRLogCallback callback;
	
	static {
		callback = OVRLogCallback.create((userData, level, message) -> System.out.println("LibOVR [" + level + "] " + MemoryUtil.memASCII(message)));
		
		OVRInitParams params = OVRInitParams.calloc()
				.LogCallback(callback)
				.Flags(OVR.ovrInit_Debug);
		int result = OVR.ovr_Initialize(params);
		if (result <= 0) {
			params.free();
			
			OVRErrorInfo info = OVRErrorInfo.malloc();
			OVR.ovr_GetLastErrorInfo(info);
			LOGGER.error(info.ErrorStringString());
			info.free();
			throw new RuntimeException("Failed to initialize OVR");
		}
		params.free();
		
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
			
			trackingState.free();
			luid.free();
			session.free();
			callback.free();
		}
	}
	
	public static void init() {
	}
}
