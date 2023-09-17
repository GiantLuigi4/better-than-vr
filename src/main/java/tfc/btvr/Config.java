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
	
	public static class DecimalOption extends Option {
		String name;
		double value;
		
		public DecimalOption(String name, double value) {
			this.name = name;
			this.value = value;
			ALL_OPTIONS.add(this);
		}
		
		protected void write(Properties properties) {
			properties.put(name, "" + value);
		}
		
		protected void read(ConfigHandler properties) {
			value = Double.parseDouble(properties.getProperty(name));
		}
		
		public double get() {
			return value;
		}
	}
	
	public static final HandOption MOTION_HAND = new HandOption("motion_hand", false);
	public static final HandOption TRACE_HAND = new HandOption("trace_hand", true);
	
	public static final BooleanOption HYBRID_MODE = new BooleanOption("flat_ui", true);
	
	public static final BooleanOption LEFT_HANDED = new BooleanOption("left_handed", false);
	
	public static final BooleanOption SMOOTH_ROTATION = new BooleanOption("smooth_rotation", false);
	public static final BooleanOption EXTRA_SMOOTH_ROTATION = new BooleanOption("extra_smooth_rotation", false);
	public static final DecimalOption ROTATION_SPEED = new DecimalOption("rotation_speed", 22.5);
	
	public static void init() {
		Properties properties = new Properties();
		
		MOTION_HAND.write(properties);
		TRACE_HAND.write(properties);
		
		HYBRID_MODE.write(properties);
		
		SMOOTH_ROTATION.write(properties);
		// this option is here for those who want it
		// donno if it's just me, but this option makes VR feel a lot worse
		EXTRA_SMOOTH_ROTATION.write(properties);
		ROTATION_SPEED.write(properties);
		
		LEFT_HANDED.write(properties);
		
		ConfigHandler hndlr = new ConfigHandler("btvr", properties);
		hndlr.loadConfig();
		
		for (Option allOption : ALL_OPTIONS) allOption.read(hndlr);
	}
}
