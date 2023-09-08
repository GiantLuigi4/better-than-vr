package tfc.btvr;

import net.fabricmc.api.ModInitializer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfc.btvr.lwjgl3.BTVRSetup;

import java.nio.IntBuffer;

// https://skarredghost.com/2018/03/15/introduction-to-openvr-101-series-what-is-openvr-and-how-to-get-started-with-its-apis/
public class BTVR implements ModInitializer {
	public static final String MOD_ID = "examplemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
//	private static final LWJGLClassLoader lwjglCL = new LWJGLClassLoader();
	
	public BTVR() {
	}
	
	@Override
	public void onInitialize() {
		LOGGER.info("ExampleMod initialized.");
	}
}
