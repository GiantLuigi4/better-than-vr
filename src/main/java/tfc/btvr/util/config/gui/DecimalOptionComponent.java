package tfc.btvr.util.config.gui;

import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.options.components.ButtonComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DecimalOptionComponent extends ButtonComponent {
	Consumer<Double> setter;
	Supplier<Double> current;
	Double def;
	private final GuiSlider slider;
	String suffix;
	double min, max;
	
	protected void setText() {
		double dv = ((this.slider.sliderValue * (max - min)) + min);
		int iv = (int) Math.round(dv * 10);
		
		this.slider.displayString = (iv / 10d) + suffix;
	}
	
	public DecimalOptionComponent(double min, double max, String translationKey, Consumer<Double> setter, Supplier<Double> current, Double def, String suffix) {
		super(translationKey);
		this.setter = setter;
		this.current = current;
		this.def = def;
		this.min = min;
		this.max = max;
		
		this.slider = new GuiSlider(0, 0, 0, 150, 20, current.get().floatValue() + suffix, current.get().floatValue());
		this.suffix = suffix;
		setText();
	}
	
	protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		this.slider.mouseClicked(mc, this.slider.xPosition + relativeMouseX, this.slider.yPosition + relativeMouseY);
//		setter.accept((double) this.slider.sliderValue);
		setText();
	}
	
	protected void buttonDragged(int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
//		setter.accept((double) this.slider.sliderValue);
		setText();
	}
	
	protected void buttonReleased(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		this.slider.mouseReleased(this.slider.xPosition + relativeMouseX, this.slider.yPosition + relativeMouseY);
		setter.accept((double) this.slider.sliderValue);
		setText();
	}
	
	protected void renderButton(
			int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY
	) {
		super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
		this.slider.xPosition = x + relativeButtonX;
		this.slider.yPosition = y + relativeButtonY;
		this.slider.width = buttonWidth;
		this.slider.height = buttonHeight;
		this.slider.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
	}
	
	public void resetValue() {
		setter.accept(def);
		this.slider.sliderValue = current.get().floatValue();
		setText();
	}
	
	public boolean isDefault() {
		return this.slider.sliderValue == def.floatValue();
	}
}

