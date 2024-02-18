package tfc.btvr.util.config.gui;

import tfc.btvr.util.config.gui.helper.MenuHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ApplyableEnumOptionComponent<T extends Enum<?>> extends EnumOptionComponent<T> {
	Runnable onApply;
	
	public ApplyableEnumOptionComponent(String valueKey, String translationKey, Consumer<T> setter, Supplier<T> current, T[] values, T def, Runnable onApply) {
		super(valueKey, translationKey, setter, current, values, def);
		
		this.onApply = onApply;
		button.setWidth(button.getWidth() - 50);
	}
	
	@Override
	protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		int v = (int) ((relativeMouseX / (double) button.width) * values.length);
		if (v >= values.length) {
			onApply.run();
		} else {
			setter.accept(values[v]);
			button.setState(current.get().ordinal());
			this.button.setText(valueKey + current.get().toString().toLowerCase());
		}
	}
	
	@Override
	protected void renderButton(int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY) {
		// super.super.renderButton
		super.renderButton(
				x, y, relativeButtonX, relativeButtonY,
				buttonWidth - 20, buttonHeight, relativeMouseX, relativeMouseY
		);
		
		int oset = relativeButtonX + button.width;
		relativeMouseY -= relativeButtonY;
		relativeMouseX -= oset;
		
		boolean hover = false;
		if (relativeMouseX > 0 && relativeMouseX < 20 && relativeMouseY < 20 && relativeMouseY > 0) {
			hover = true;
		}
		
		MenuHelper.drawButton(
				mc,
				x, y,
				20,
				oset, relativeButtonY,
				hover, "✔"
		);
	}
}
