package tfc.btvr;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://skarredghost.com/2018/03/15/introduction-to-openvr-101-series-what-is-openvr-and-how-to-get-started-with-its-apis/
public class BTVR implements ModInitializer {
	public static final String MOD_ID = "examplemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	
	public BTVR() {
	}
	
	@Override
	public void onInitialize() {
		LOGGER.info("ExampleMod initialized.");
		
		Config.init();
	}
}
