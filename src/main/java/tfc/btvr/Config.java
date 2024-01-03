package tfc.btvr;

import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRMode;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.ArrayList;
import java.util.Properties;

public class Config {
	private static final ArrayList<Option> ALL_OPTIONS = new ArrayList<>();
	
	private static abstract class Option {
		
		protected abstract void write(Properties properties);
		
		protected abstract void read(ConfigHandler properties);
	}
	
	enum Hand {
		LEFT, RIGHT, MAIN
	}
	
	public static class HandOption extends Option {
		String name;
		Hand value;
		
		public HandOption(String name, Hand defaultV) {
			this.name = name;
			this.value = defaultV;
			ALL_OPTIONS.add(this);
		}
		
		protected void write(Properties properties) {
			properties.put(name, value.name().toLowerCase());
		}
		
		protected void read(ConfigHandler properties) {
			switch (properties.getString(name)) {
				case "left":
					value = Hand.LEFT;
					break;
				case "right":
					value = Hand.RIGHT;
					break;
//				case "main":
//					value = Hand.MAIN;
//					break;
				default:
					value = Hand.MAIN;
					break;
			}
		}
		
		public SDevice get() {
			switch (value) {
				case LEFT:
					return SDevice.getDeviceForRole(DeviceType.LEFT_HAND);
				case RIGHT:
					return SDevice.getDeviceForRole(DeviceType.RIGHT_HAND);
				default:
					return SDevice.getDeviceForRole(Config.LEFT_HANDED.get() ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND);
			}
		}
		
		public DeviceType getType() {
			switch (value) {
				case LEFT:
					return DeviceType.LEFT_HAND;
				case RIGHT:
					return DeviceType.RIGHT_HAND;
				default:
					return Config.LEFT_HANDED.get() ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND;
			}
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
	
	public static class ModeOption extends Option {
		String name;
		VRMode value;
		
		public ModeOption(String name, VRMode value) {
			this.name = name;
			this.value = value;
			ALL_OPTIONS.add(this);
		}
		
		protected void write(Properties properties) {
			properties.put(name, value.cfgName());
		}
		
		protected void read(ConfigHandler properties) {
			switch (properties.getString(name)) {
				case "steam":
					value = VRMode.STEAM_VR;
					break;
				case "oculus":
					value = VRMode.OCULUS_VR;
					break;
				case "pancake":
					value = VRMode.NONE;
					break;
			}
		}
		
		public VRMode get() {
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
	
	public static final HandOption MOTION_HAND = new HandOption("motion_hand", Hand.LEFT);
	public static final HandOption TRACE_HAND = new HandOption("trace_hand", Hand.RIGHT);
	public static final HandOption INTERACTION_HAND = new HandOption("interaction_hand", Hand.MAIN);
	
	public static final BooleanOption HYBRID_MODE = new BooleanOption("flat_ui", true);
	
	public static final BooleanOption LEFT_HANDED = new BooleanOption("left_handed", false);
	
	public static final BooleanOption SMOOTH_ROTATION = new BooleanOption("smooth_rotation", false);
	public static final BooleanOption EXTRA_SMOOTH_ROTATION = new BooleanOption("extra_smooth_rotation", false);
	public static final DecimalOption ROTATION_SPEED = new DecimalOption("rotation_speed", 22.5);
	
	public static final ModeOption MODE = new ModeOption("mode", BTVRSetup.getDefaultMode());
	
	public static void init() {
		Properties properties = new Properties();
		
		MOTION_HAND.write(properties);
		TRACE_HAND.write(properties);
		INTERACTION_HAND.write(properties);
		
		HYBRID_MODE.write(properties);
		
		SMOOTH_ROTATION.write(properties);
		// this option is here for those who want it
		// donno if it's just me, but this option makes VR feel a lot worse
		EXTRA_SMOOTH_ROTATION.write(properties);
		ROTATION_SPEED.write(properties);
		
		LEFT_HANDED.write(properties);
		
		MODE.write(properties);
		
		ConfigHandler hndlr = new ConfigHandler("btvr", properties);
		hndlr.loadConfig();
		
		for (Option allOption : ALL_OPTIONS) allOption.read(hndlr);
	}
}
