package tfc.btvr.util.config.gui.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTexturedButton;

public class MenuHelper {
	public static void drawButton(
			Minecraft mc,
			int x, int y,
			int width,
			int offset, int relativeButtonY,
			boolean hover,
			String text,
			boolean disabled
	) {
		GuiTexturedButton applyL = new GuiTexturedButton(
				0, "/gui/gui.png", width, 0,
				0, 86 - 20 * 2 - (disabled ? 20 : 0),
				20, 20
		);
		GuiTexturedButton applyR = new GuiTexturedButton(
				0, "/gui/gui.png", width, 0,
				200 - (width / 2), 86 - 20 * 2 - (disabled ? 20 : 0),
				20, 20
		);
		
		applyL.setX(x + offset);
		applyL.setY(y + relativeButtonY);
		applyL.setHeight(20);
		applyL.setWidth(width / 2);
		
		applyL.drawButton(mc, (!disabled && hover) ? applyL.getX() : -1, applyL.getY());
		
		applyR.setX(x + (width / 2) + offset);
		applyR.setY(y + relativeButtonY);
		applyR.setHeight(20);
		applyR.setWidth(width / 2);
		applyR.drawButton(mc, (!disabled && hover) ? applyR.getX() : -1, applyR.getY());
		
		int k = disabled ? 10526880 : (hover ? 16777120 : 14737632);
		applyL.drawStringCentered(mc.fontRenderer, text, applyL.xPosition + (width / 2), applyL.yPosition + (applyL.height - 8) / 2, k);
	}
	
	public static void drawButton(
			Minecraft mc,
			int x, int y,
			int width,
			int offset, int relativeButtonY,
			boolean hover,
			String text
	) {
		drawButton(mc, x, y, width, offset, relativeButtonY, hover, text, false);
	}
}
