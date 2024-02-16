package tfc.btvr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRMode;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.util.config.ApplyableEnumOptionComponent;
import tfc.btvr.util.config.BooleanOptionElement;
import tfc.btvr.util.config.DecimalOptionComponent;
import tfc.btvr.util.config.EnumOptionComponent;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.ArrayList;
import java.util.Properties;

import static net.minecraft.client.gui.options.data.OptionsPages.register;

public class Config {
	private static final ArrayList<Option> ALL_OPTIONS = new ArrayList<>();
	
	private static OptionsPage VR = null;
	
	protected static void update(Object oldV, Object newV) {
		if (oldV != newV && !oldV.equals(newV))
			write().writeDefaultConfig();
	}
	
	public static OptionsPage getVRPage() {
		if (VR == null) {
			VR = new OptionsPage("btvr.gui.options.page.vr.title");
			VR.withComponent(
					new OptionsCategory("btvr.gui.options.page.vr.category.hands")
							.withComponent(new EnumOptionComponent<>("btvr.gui.option.page.vr.value.hand.", "btvr.gui.options.page.vr.motion_hand", (v) -> update(MOTION_HAND.value, MOTION_HAND.value = v), () -> MOTION_HAND.value, Hand.values(), MOTION_HAND.def))
							.withComponent(new EnumOptionComponent<>("btvr.gui.option.page.vr.value.hand.", "btvr.gui.options.page.vr.trace_hand", (v) -> update(TRACE_HAND.value, TRACE_HAND.value = v), () -> TRACE_HAND.value, Hand.values(), TRACE_HAND.def))
							.withComponent(new EnumOptionComponent<>("btvr.gui.option.page.vr.value.hand.", "btvr.gui.options.page.vr.interaction_hand", (v) -> update(INTERACTION_HAND.value, INTERACTION_HAND.value = v), () -> INTERACTION_HAND.value, Hand.values(), INTERACTION_HAND.def))
							.withComponent(new BooleanOptionElement("options.", "btvr.gui.options.page.vr.left_handed", (v) -> update(LEFT_HANDED.value, LEFT_HANDED.value = v), LEFT_HANDED::get, LEFT_HANDED.def))
			);
			VR.withComponent(
					new OptionsCategory("btvr.gui.options.page.vr.category.general")
							.withComponent(new ApplyableEnumOptionComponent<>("btvr.gui.option.page.vr.value.mode.", "btvr.gui.options.page.vr.vr_mode", (v) -> update(MODE.value, MODE.value = v), () -> MODE.value, VRMode.values(), VRMode.NONE, () -> {
//								BTVRSetup.whenTheGameHasBeenRequestedToShutdownIShouldAlsoShutdownTheSteamVRAndOVRLogicToAvoidCreatingProblemsAndDeadlocksLol(
//										VRManager.getActiveMode()
//								);
								VRManager.shutdown();
								VRManager.setMode(MODE.value);
							}))
							.withComponent(new BooleanOptionElement("options.", "btvr.gui.options.page.vr.hybrid_mode", (v) -> update(HYBRID_MODE.value, HYBRID_MODE.value = v), HYBRID_MODE::get, HYBRID_MODE.def))
			);
			VR.withComponent(
					new OptionsCategory("btvr.gui.options.page.vr.category.rotation")
							.withComponent(new BooleanOptionElement("options.", "btvr.gui.options.page.vr.smooth_rotation", (v) -> update(SMOOTH_ROTATION.value, SMOOTH_ROTATION.value = v), SMOOTH_ROTATION::get, SMOOTH_ROTATION.def))
							.withComponent(new BooleanOptionElement("options.", "btvr.gui.options.page.vr.extra_smooth_rotation", (v) -> update(EXTRA_SMOOTH_ROTATION.value, EXTRA_SMOOTH_ROTATION.value = v), EXTRA_SMOOTH_ROTATION::get, EXTRA_SMOOTH_ROTATION.def))
							.withComponent(new DecimalOptionComponent(0, 90, "btvr.gui.options.page.vr.extra_smooth_rotation", (v) -> update(ROTATION_SPEED.value, ROTATION_SPEED.value = Math.round(v * 900) / 10d), () -> (ROTATION_SPEED.get() / 90f), (ROTATION_SPEED.def / 90f), ""))
			);
			register(VR);
		}
		VR.initComponents(Minecraft.getMinecraft(Minecraft.class));
		return VR;
	}
	
	private static abstract class Option {
		
		protected abstract void write(Properties properties);
		
		protected abstract void read(ConfigHandler properties);
	}
	
	enum Hand {
		LEFT, OFF, MAIN, RIGHT
	}
	
	public static class HandOption extends Option {
		String name;
		Hand def;
		Hand value;
		
		public HandOption(String name, Hand defaultV) {
			this.name = name;
			this.def = defaultV;
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
				case "off":
					value = Hand.OFF;
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
				case OFF:
					return SDevice.getDeviceForRole(Config.LEFT_HANDED.get() ? DeviceType.RIGHT_HAND : DeviceType.LEFT_HAND);
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
				case OFF:
					return Config.LEFT_HANDED.get() ? DeviceType.RIGHT_HAND : DeviceType.LEFT_HAND;
				default:
					return Config.LEFT_HANDED.get() ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND;
			}
		}
	}
	
	public static class BooleanOption extends Option {
		String name;
		boolean def;
		boolean value;
		
		public BooleanOption(String name, boolean value) {
			this.name = name;
			this.def = value;
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
				case "off":
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
		double def;
		
		public DecimalOption(String name, double value) {
			this.name = name;
			this.def = value;
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
	
	public static ConfigHandler write() {
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
		
		return new ConfigHandler("btvr", properties);
	}
	
	public static void init() {
		ConfigHandler hndlr = write();
		hndlr.loadConfig();
		
		for (Option allOption : ALL_OPTIONS) allOption.read(hndlr);
	}
}
