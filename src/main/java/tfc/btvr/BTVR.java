package tfc.btvr;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.packet.Packet;
import tfc.btvr.menu.MenuWorld;
import tfc.btvr.mp.VRSuperPacket;

import java.lang.reflect.Method;

// https://skarredghost.com/2018/03/15/introduction-to-openvr-101-series-what-is-openvr-and-how-to-get-started-with-its-apis/
public class BTVR implements ModInitializer {
	public static MenuWorld menuWorld;
	
	@Override
	public void onInitialize() {
		try {
			Method m = Packet.class.getDeclaredMethod("addIdClassMapping", int.class, boolean.class, boolean.class, Class.class);
			m.setAccessible(true);
			m.invoke(
					null, 145,
					true, true,
					VRSuperPacket.class
			);
		} catch (Throwable ignored) {
		}
	}
	
	public static EntityPlayer getMenuPlayer() {
		if (menuWorld != null)
			return menuWorld.myPlayer;
		else return null;
	}
}
