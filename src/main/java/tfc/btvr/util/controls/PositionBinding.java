package tfc.btvr.util.controls;

import tfc.btvr.lwjgl3.openvr.VRControllerInput;

import java.util.function.BiConsumer;

public class PositionBinding extends VRBinding {
	BiConsumer<Double, Double> doubleTrouble;
	
	public PositionBinding(String group, String name, BiConsumer<Double, Double> action) {
		super(group, name);
		this.doubleTrouble = action;
	}
	
	@Override
	public void tick() {
		float[] value = VRControllerInput.getJoystick(group, name);
		
		doubleTrouble.accept((double) value[0], (double) value[1]);
	}
}
