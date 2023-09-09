package tfc.btvr;

import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class Config {
	public static class HandOption {
		String name;
		boolean right;
		
		public HandOption(String name, boolean right) {
			this.name = name;
			this.right = right;
		}
		
		protected void write(Properties properties) {
			properties.put(name, right ? "right" : "left");
		}
		
		protected void read(ConfigHandler properties) {
			right = properties.getString(name).equals("right");
		}
		
		public Device get() {
			return Device.getDeviceForRole(
					right ? DeviceType.RIGHT_HAND : DeviceType.LEFT_HAND
			);
		}
	}
	
	public static final HandOption MOTION_HAND = new HandOption("motion_hand", false);
	public static final HandOption TRACE_HAND = new HandOption("trace_hand", true);
	
	public static void init() {
		Properties properties = new Properties();
		
		MOTION_HAND.write(properties);
		TRACE_HAND.write(properties);
		
		ConfigHandler hndlr = new ConfigHandler("btvr", properties);
		hndlr.loadConfig();
		
		MOTION_HAND.read(hndlr);
		TRACE_HAND.read(hndlr);
	}
}
