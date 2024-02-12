package tfc.btvr.util.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.core.lang.I18n;
import org.lwjgl.opengl.GL11;

public class MultiStateButton extends GuiButton {
	private int state;
	private int states;
	
	public MultiStateButton(int states, int id, int xPosition, int yPosition, int state, String text) {
		super(id, xPosition, yPosition, "");
		this.state = state;
		this.states = states;
		this.displayString = text;
	}
	
	public MultiStateButton(int states, int id, int xPosition, int yPosition, int width, int height, int state, String text) {
		super(id, xPosition, yPosition, width, height, "");
		this.state = state;
		this.states = states;
		this.displayString = text;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			boolean flag = mouseX >= this.xPosition
					&& mouseY >= this.yPosition
					&& mouseX < this.xPosition + this.width
					&& mouseY < this.yPosition + this.height;
			int i = this.getButtonState(flag);
			int j = ((this.width / states) * state);
			GL11.glBindTexture(3553, mc.renderEngine.getTexture("/gui/gui.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int div = states * 2;
			
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46, this.width / 2, this.height);
			
			this.drawTexturedModalRect(
					this.xPosition + j,
					this.yPosition,
					0,
					46 + i * 20,
					this.width / div,
					this.height
			);
			this.drawTexturedModalRect(
					this.xPosition + this.width / div + j,
					this.yPosition,
					200 - this.width / div,
					46 + i * 20,
					this.width / div,
					this.height
			);
			
			this.mouseDragged(mc, mouseX, mouseY);
			int k;
			switch (i) {
				case 0:
					k = 10526880;
					break;
				case 1:
					k = 14737632;
					break;
				default:
					k = 16777120;
			}
			
			this.drawStringCentered(fontrenderer, this.displayString, this.xPosition + j + this.width / div, this.yPosition + (this.height - 8) / 2, k);
		}
	}
	
	public void setText(String s) {
		this.displayString = I18n.getInstance().translateKey(s);
	}
}
