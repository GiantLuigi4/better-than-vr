package tfc.btvr.util.config.gui.menu;

import net.minecraft.client.gui.GuiTexturedButton;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.core.lang.I18n;

public class MenuCaptureComponent extends ButtonComponent {
	public MenuCaptureComponent(String translationKey) {
		super(translationKey);
	}
	
	@Override
	public void resetValue() {
	}
	
	@Override
	public boolean isDefault() {
		return true;
	}
	
	@Override
	protected void buttonClicked(int i, int j, int k, int l, int m, int n, int o) {
	
	}
	
	@Override
	protected void renderButton(int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY) {
		GuiTexturedButton applyL = new GuiTexturedButton(
				0, "/gui/gui.png", buttonWidth, 0,
				0, 86 - 20 * 2,
				buttonWidth / 2, 20
		);
		GuiTexturedButton applyR = new GuiTexturedButton(
				0, "/gui/gui.png", buttonWidth, 0,
				200 - (buttonWidth / 2), 86 - 20 * 2,
				buttonWidth / 2, 20
		);
		
		relativeMouseX -= relativeButtonX;
		
		applyL.setX(x + relativeButtonX);
		applyL.setY(y + relativeButtonY);
		applyL.setHeight(20);
		applyL.setWidth(buttonWidth / 2);
		
		boolean hover = false;
		if (relativeMouseX > 0 && relativeMouseX < buttonWidth && relativeMouseY < 20 && relativeMouseY > 0) {
			hover = true;
		}
		
		applyL.drawButton(mc, hover ? applyL.getX() : -1, relativeMouseY + applyL.getY());
		
		applyR.setX(x + (buttonWidth / 2) + relativeButtonX);
		applyR.setY(y + relativeButtonY);
		applyR.setHeight(20);
		applyR.setWidth(buttonWidth / 2);
		applyR.drawButton(mc, hover ? applyR.getX() : -1, relativeMouseY + applyR.getY());
		
		int k = hover ? 16777120 : 14737632;
		String text = I18n.getInstance().translateKey("btvr.gui.option.page.vr.open_menu");
		applyL.drawStringCentered(mc.fontRenderer, text, applyL.xPosition + buttonWidth / 2, applyL.yPosition + (applyL.height - 8) / 2, k);
	}
}
