package tfc.btvr.util.config.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import tfc.btvr.util.config.gui.helper.MenuHelper;

import java.util.ArrayList;
import java.util.function.Supplier;

public class DropdownElement extends GuiButton {
	boolean open = false;
	
	public DropdownElement(int id, int xPosition, int yPosition, int width, int height, String text) {
		super(id, xPosition, yPosition, width, height, text);
	}
	
	ArrayList<String> strs = new ArrayList<>();
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		this.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
		this.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, -6250336);
		this.drawRect(this.xPosition + 2, this.yPosition + 2, this.xPosition + this.width - 2, this.yPosition + this.height - 2, -16777216);
		
		this.drawStringCentered(mc.fontRenderer, this.displayString, (this.xPosition + this.width / 2), this.yPosition + (this.height - 8) / 2, 14737632);
		
		if (open) {
			this.drawRect(this.xPosition, this.yPosition + 20, this.xPosition + this.width, this.yPosition + this.height + 20, 1602191231);
			this.drawStringCentered(mc.fontRenderer, "1/1", xPosition + (width) / 2, 20 + yPosition + (height - 8) / 2, 14737632);
			
			MenuHelper.drawButton(
					mc,
					xPosition, yPosition + 20,
					20,
					0, 0,
					(
							mouseX > 0 && mouseX < 20 &&
									mouseY > 20 && mouseY < 40
					), "<"
			);
			MenuHelper.drawButton(
					mc,
					xPosition + width - 20, yPosition + 20,
					20,
					0, 0,
					(
							mouseX > width - 20 && mouseX < width &&
									mouseY > 20 && mouseY < 40
					), ">"
			);
			
			for (int i = 0; i < 7; i++) {
				int yOff = (i + 2) * 20;
				
				String txt = "";
				if (i < strs.size()) {
					txt = strs.get(i).split(",")[2].trim();
					txt = txt.substring(0, txt.length() - 4);
				}
				
				MenuHelper.drawButton(
						mc,
						xPosition, yPosition + yOff,
						width,
						0, 0,
						(
								mouseX > 0 && mouseX < width &&
										mouseY > yOff && mouseY < yOff + 20
						), txt,
						txt.isEmpty()
				);
			}
		}
	}
	
	public void collect(Supplier<ArrayList<String>> slots) {
		strs = slots.get();
	}
}
