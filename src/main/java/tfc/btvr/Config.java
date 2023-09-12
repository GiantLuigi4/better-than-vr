package tfc.btvr;

import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.ArrayList;
import java.util.Properties;

public class Config {
	private static final ArrayList<Option> ALL_OPTIONS = new ArrayList<>();
	
	private static abstract class Option {
		protected abstract void write(Properties properties);
		
		protected abstract void read(ConfigHandler properties);
	}
	
	public static class HandOption extends Option {
		String name;
		boolean right;
		
		public HandOption(String name, boolean right) {
			this.name = name;
			this.right = right;
			ALL_OPTIONS.add(this);
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
	
	public static class BooleanOption extends Option {
		String name;
		boolean value;
		
		public BooleanOption(String name, boolean value) {
			this.name = name;
			this.value = value;
			ALL_OPTIONS.add(this);
		}
		
		protected void write(Properties properties) {
			properties.put(name, "" + value);
		}
		
		protected void read(ConfigHandler properties) {
			value = properties.getBoolean(name);
		}
		
		public boolean get() {
			return value;
		}
	}
	
	public static final HandOption MOTION_HAND = new HandOption("motion_hand", false);
	public static final HandOption TRACE_HAND = new HandOption("trace_hand", true);
	public static final BooleanOption HYBRID_MODE = new BooleanOption("flat_ui", true);
	
	public static void init() {
		Properties properties = new Properties();
		
		MOTION_HAND.write(properties);
		TRACE_HAND.write(properties);
		
		HYBRID_MODE.write(properties);
		
		ConfigHandler hndlr = new ConfigHandler("btvr", properties);
		hndlr.loadConfig();
		
		for (Option allOption : ALL_OPTIONS) allOption.read(hndlr);
	}
}
