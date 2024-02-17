package tfc.btvr.util.config.gui;

import net.minecraft.client.gui.GuiTexturedButton;

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
		if (this.resetButton.enabled) {
			this.resetButton.xPosition = x + relativeButtonX - 20;
			this.resetButton.yPosition = y + relativeButtonY;
			this.resetButton.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
		}
		
		this.button.xPosition = x + relativeButtonX;
		this.button.yPosition = y + relativeButtonY;
		this.button.width = buttonWidth - 20;
		this.button.height = buttonHeight;
		this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
		
		GuiTexturedButton applyL = new GuiTexturedButton(
				0, "/gui/gui.png", button.getWidth(), 0,
				0, 86 - 20 * 2,
				20 / 2, 20
		);
		GuiTexturedButton applyR = new GuiTexturedButton(
				0, "/gui/gui.png", button.getWidth(), 0,
				200 - 10, 86 - 20 * 2,
				20 / 2, 20
		);
		
		int oset = relativeButtonX + button.width;
		relativeMouseY -= relativeButtonY;
		relativeMouseX -= oset;
		
		applyL.setX(x + oset);
		applyL.setY(y + relativeButtonY);
		applyL.setHeight(20);
		applyL.setWidth(10);
		
		boolean hover = false;
		if (relativeMouseX > 0 && relativeMouseX < 20 && relativeMouseY < 20 && relativeMouseY > 0) {
			hover = true;
		}
		
		applyL.drawButton(mc, hover ? applyL.getX() : -1, relativeMouseY + applyL.getY());
		
		applyR.setX(x + 10 + oset);
		applyR.setY(y + relativeButtonY);
		applyR.setHeight(20);
		applyR.setWidth(10);
		applyR.drawButton(mc, hover ? applyR.getX() : -1, relativeMouseY + applyR.getY());
		
		int k = hover ? 16777120 : 14737632;
		applyL.drawStringCentered(mc.fontRenderer, "✔", applyL.xPosition + 10, applyL.yPosition + (applyL.height - 8) / 2, k);
	}
}
