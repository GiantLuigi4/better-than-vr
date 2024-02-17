package tfc.btvr.util.gestures;

import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.util.config.Config;
import tfc.btvr.util.gestures.immersion.AttackGesture;
import tfc.btvr.util.gestures.immersion.EatingGesture;
import tfc.btvr.util.gestures.immersion.MiningGesture;

public class GestureControllers {
	private static GestureController HEAD;
	private static GestureController MAIN_HAND;
	private static GestureController OFF_HAND;
	
	/**
	 * Mods should mixin to this if adding their own gestures
	 * This will be called whenever the config for the main hand is updated
	 */
	public static void registerGestures() {
		HEAD = new GestureController(DeviceType.HEAD);
		
		MAIN_HAND = new GestureController(Config.INTERACTION_HAND.getType());
		OFF_HAND = new GestureController(Config.INTERACTION_HAND.getType() == DeviceType.RIGHT_HAND ? DeviceType.LEFT_HAND : DeviceType.RIGHT_HAND);
		
		MAIN_HAND.addGesture(new EatingGesture());
		MAIN_HAND.addGesture(new AttackGesture());
		MAIN_HAND.addGesture(new MiningGesture());
		
		OFF_HAND.addGesture(new AttackGesture());
		OFF_HAND.addGesture(new MiningGesture());
	}
	
	public static GestureController getHeadController() {
		return HEAD;
	}
	
	public static GestureController getMainHandController() {
		return MAIN_HAND;
	}
	
	public static GestureController getOffHandController() {
		return OFF_HAND;
	}
	
	static {
		registerGestures();
	}
}
