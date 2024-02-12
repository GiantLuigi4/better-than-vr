package tfc.btvr.util.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSwitchButton;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.core.lang.I18n;

import java.util.function.Consumer;
import java.util.function.Supplier;


public class BooleanOptionElement extends ButtonComponent {
	String valueKey;
	Consumer<Boolean> setter;
	Supplier<Boolean> current;
	Boolean def;
	private final GuiSwitchButton button;
	
	public BooleanOptionElement(String valueKey, String translationKey, Consumer<Boolean> setter, Supplier<Boolean> current, Boolean def) {
		super(translationKey);
		this.setter = setter;
		this.current = current;
		this.def = def;
		this.valueKey = valueKey;
		
		this.button = new GuiSwitchButton(0, 0, 0, 150, 20, def, "", "");
		this.button.setOn(def);
		this.button.displayString = I18n.getInstance().translateKey(valueKey + (button.isOn() ? "on" : "off"));
	}
	
	public void resetValue() {
		this.setter.accept(def);
		this.button.setOn(def);
		this.button.displayString = I18n.getInstance().translateKey(valueKey + (button.isOn() ? "on" : "off"));
	}
	
	public boolean isDefault() {
		return current.get() == def;
	}
	
	protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		setter.accept(!current.get().booleanValue());
		this.button.setOn(current.get());
		this.button.displayString = I18n.getInstance().translateKey(valueKey + (button.isOn() ? "on" : "off"));
	}
	
	protected void renderButton(
			int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY
	) {
		super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
		this.button.xPosition = x + relativeButtonX;
		this.button.yPosition = y + relativeButtonY;
		this.button.width = buttonWidth;
		this.button.height = buttonHeight;
		this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
	}
	
	@Override
	public void init(Minecraft mc) {
		super.init(mc);
		
		setter.accept(current.get());
		this.button.setOn(current.get());
		this.button.displayString = I18n.getInstance().translateKey(valueKey + (button.isOn() ? "on" : "off"));
	}
}
