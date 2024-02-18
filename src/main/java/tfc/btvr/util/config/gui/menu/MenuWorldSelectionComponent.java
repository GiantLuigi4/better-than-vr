package tfc.btvr.util.config.gui.menu;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundType;
import org.lwjgl.opengl.GL11;
import tfc.btvr.menu.MenuWorld;
import tfc.btvr.util.config.gui.helper.MenuHelper;

public class MenuWorldSelectionComponent implements OptionsComponent {
	protected static final Minecraft mc = Minecraft.getMinecraft(ButtonComponent.class);
	
	DropdownElement dropdownElement = new DropdownElement(
			0, 0, 0,
			20, 20,
			"laz/cliffs"
	);
	
	String translationKey;
	
	public MenuWorldSelectionComponent(String translationKey) {
		this.translationKey = translationKey;
	}
	
	@Override
	public int getHeight() {
		if (dropdownElement.open)
			return 24 + (20 * 8);
		return 24;
	}
	
	@Override
	public void render(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		FontRenderer fontrenderer = mc.fontRenderer;
		String s = I18n.getInstance().translateKey(this.translationKey);
		int i = -1;
		if (relativeMouseX >= 0 && relativeMouseX <= width && relativeMouseY >= 2 && relativeMouseY <= this.getHeight() - 2) {
			i = -96;
		}
		
		fontrenderer.drawStringWithShadow(s, x, y + 24 / 2 - 4, i);
		this.renderButton(x, y, width - 120, 2, 120, 20, relativeMouseX, relativeMouseY);
		int j = fontrenderer.getStringWidth(s);
		int k = x + width - 120 - 8;
		
		this.drawRect(x + j + 8, y + 24 / 2, k, y + 24 / 2 + 1, 1602191231);
	}
	
	protected void renderButton(
			int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY
	) {
		this.dropdownElement.xPosition = x + relativeButtonX;
		this.dropdownElement.yPosition = y + relativeButtonY;
		this.dropdownElement.width = buttonWidth;
		this.dropdownElement.height = buttonHeight;
		dropdownElement.drawButton(mc, relativeMouseX - relativeButtonX, relativeMouseY - relativeButtonY);
		
		int oset = relativeButtonX;
		relativeMouseY -= relativeButtonY;
		relativeMouseX -= oset;
		
		boolean hover = false;
		if (relativeMouseX > 0 && relativeMouseX < buttonWidth && relativeMouseY < 20 && relativeMouseY > 0) {
			hover = true;
		}
		
		String c = (!dropdownElement.open) ? "▷" : "▽";
		
		MenuHelper.drawButton(
				mc,
				x + mc.fontRenderer.getStringWidth(c) + 3, y,
				0,
				relativeButtonX, relativeButtonY,
				hover, c
		);
	}
	
	protected void drawRect(int minX, int minY, int maxX, int maxY, int argb) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(argb & 16777215, argb >> 24 & 0xFF);
		tessellator.addVertex(minX, maxY, 0.0);
		tessellator.addVertex(maxX, maxY, 0.0);
		tessellator.addVertex(maxX, minY, 0.0);
		tessellator.addVertex(minX, minY, 0.0);
		tessellator.draw();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
	}
	
	@Override
	public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		if (relativeMouseX >= width - 120 && relativeMouseX <= width) {
			if (relativeMouseY >= 2 && relativeMouseY <= 22) {
				mc.sndManager.playSound("random.click", SoundType.GUI_SOUNDS, 1.0F, 1.0F);
				dropdownElement.open = !dropdownElement.open;
				if (dropdownElement.open)
					dropdownElement.collect(() -> {
						try {
							return MenuWorld.listWorlds();
						} catch (Throwable err) {
							err.printStackTrace();
						}
						return Lists.newArrayList();
					});
			} else {
				if (dropdownElement.open) {
					int elem = (relativeMouseY - (20 * 2)) / 20;
					if (relativeMouseY > 40)
						System.out.println(elem);
				}
			}
		}
	}
	
	@Override
	public void onMouseMove(int i, int j, int k, int l, int m) {
	}
	
	@Override
	public void onMouseRelease(int i, int j, int k, int l, int m, int n) {
	}
	
	@Override
	public void onKeyPress(int i, char c) {
	}
	
	@Override
	public boolean matchesSearchTerm(String term) {
		return I18n.getInstance().translateKey(this.translationKey).toLowerCase().contains(term.toLowerCase());
	}
}
