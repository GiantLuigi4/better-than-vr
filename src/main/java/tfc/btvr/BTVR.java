package tfc.btvr;

import net.fabricmc.api.ModInitializer;

// https://skarredghost.com/2018/03/15/introduction-to-openvr-101-series-what-is-openvr-and-how-to-get-started-with-its-apis/
public class BTVR implements ModInitializer {
	@Override
	public void onInitialize() {
		Config.init();
	}
}
