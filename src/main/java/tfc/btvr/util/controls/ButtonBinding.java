package tfc.btvr.util.controls;

import tfc.btvr.lwjgl3.openvr.SVRControllerInput;

public class ButtonBinding extends VRBinding {
	boolean isPressed = false;
	
	Runnable rising, active, falling;
	
	private static final Runnable dummy = () -> {
	};
	
	String group, name;
	
	public ButtonBinding(String group, String name, Runnable rising, Runnable active, Runnable falling) {
		super(group, name);
		this.group = group;
		this.name = name;
		
		if (rising == null) rising = dummy;
		if (falling == null) falling = dummy;
		if (active == null) active = dummy;
		
		this.rising = rising;
		this.active = active;
		this.falling = falling;
	}
	
	// currently, I only need rising, but in the future I might need more
	@SuppressWarnings("ConstantValue") // leaving this as is 'cuz it improves readability, I think
	public void tick() {
		boolean pressed = SVRControllerInput.getInput(group, name);
		
		if (pressed && !isPressed) rising.run();
		else if (pressed && isPressed) active.run();
		else if (!pressed && isPressed) falling.run();
		
		this.isPressed = pressed;
	}
	
	public void forceRelease() {
		isPressed = false;
	}
}
