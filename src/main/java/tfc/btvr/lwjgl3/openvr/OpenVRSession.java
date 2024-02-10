package tfc.btvr.lwjgl3.openvr;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRInput;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfc.btvr.lwjgl3.BTVRSetup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.IntBuffer;

public class OpenVRSession {
	private static int token = -1;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("nr::init::openvr");
	
	protected static void extractResource(String name) {
		try {
			File fl = new File(FabricLoader.getInstance().getGameDir() + "/vr/" + name);
			if (!fl.exists()) {
				InputStream is = BTVRSetup.class.getClassLoader().getResourceAsStream("btvr/" + name);
				if (is != null) {
					fl.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(fl);
					fos.write(is.readAllBytes());
					fos.flush();
					fos.close();
					is.close();
				}
			}
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static void setup() {
		if (VR.VR_IsRuntimeInstalled()) {
			MemoryStack stack = MemoryStack.stackPush();
			IntBuffer peError = stack.mallocInt(1);
			token = VR.VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene);
			OpenVR.create(token);
		} else {
			LOGGER.error("OpenVR is not installed");
			System.exit(-1);
		}
		
		LOGGER.info("VR Loaded, token = " + token);
		
		LOGGER.info("Writing actions");
		extractResource("actions.json");
		extractResource("bindings/knuckles.json");
		extractResource("bindings/oculus_touch.json");
		extractResource("bindings/vive_controller.json");
		extractResource("bindings/hpmotioncontroller.json");
		extractResource("bindings/holographic_controller.json");
		extractResource("bindings/vive_cosmos_controller.json");
		
		LOGGER.info("Loading actions");
		
		int err = VRInput.VRInput_SetActionManifestPath(FabricLoader.getInstance().getGameDir().toAbsolutePath() + "/vr/actions.json");
		if (err != 0) {
			System.out.println("Actions setup with error: " + err);
		}
		
		for (int i = 0; i < VR.k_unMaxTrackedDeviceCount; i++) {
			int classCallback = VRSystem.VRSystem_GetTrackedDeviceClass(i);
			if (classCallback == VR.ETrackedDeviceClass_TrackedDeviceClass_Controller || classCallback == VR.ETrackedDeviceClass_TrackedDeviceClass_GenericTracker || i == 0) {
				IntBuffer error = BufferUtils.createIntBuffer(1);
				LOGGER.info("Device: " + i);
				String controllerName = VRSystem.VRSystem_GetStringTrackedDeviceProperty(i, VR.ETrackedDeviceProperty_Prop_TrackingSystemName_String, error);
				LOGGER.info(controllerName);
				String controllerType = VRSystem.VRSystem_GetStringTrackedDeviceProperty(i, VR.ETrackedDeviceProperty_Prop_ControllerType_String, error);
				LOGGER.info(controllerType);
				String manufacturerName = VRSystem.VRSystem_GetStringTrackedDeviceProperty(i, VR.ETrackedDeviceProperty_Prop_ManufacturerName_String, error);
				LOGGER.info(manufacturerName);
				int role = VRSystem.VRSystem_GetInt32TrackedDeviceProperty(i, VR.ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32, error);
				LOGGER.info("" + role);
				int cls = VRSystem.VRSystem_GetInt32TrackedDeviceProperty(i, VR.ETrackedDeviceProperty_Prop_DeviceClass_Int32, error);
				LOGGER.info("" + cls);
			}
		}
	}
	
	public static void end() {
		token = -1;
		VR.VR_ShutdownInternal();
	}
}
