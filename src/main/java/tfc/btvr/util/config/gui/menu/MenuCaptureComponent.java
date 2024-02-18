package tfc.btvr.util.config.gui.menu;

import net.minecraft.client.gui.GuiTexturedButton;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.core.lang.I18n;
import tfc.btvr.util.config.gui.helper.MenuHelper;

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
		
		boolean hover = false;
		if (relativeMouseX > 0 && relativeMouseX < buttonWidth && relativeMouseY < 20 && relativeMouseY > 0) {
			hover = true;
		}
		
		MenuHelper.drawButton(
				mc, x, y,
				buttonWidth,
				relativeButtonX, relativeButtonY,
				hover,
				I18n.getInstance().translateKey("btvr.gui.option.page.vr.open_menu"),
				mc.theWorld == null
		);
	}
}
