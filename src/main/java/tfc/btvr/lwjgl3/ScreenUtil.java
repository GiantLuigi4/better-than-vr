package tfc.btvr.lwjgl3;

import net.minecraft.client.gui.GuiScreen;

public class ScreenUtil {
	public static class Button {
		public boolean event;
		public boolean down;
		public int x, y;
		
		public int id;
		
		public Button(int id) {
			this.id = id;
		}
	}
	
	public static Button left = new Button(0), right = new Button(1);
	
	public static void click(int x, int y, GuiScreen screen, boolean leftPressed, boolean pressed) {
		Button update = leftPressed ? left : right;
		
		if (update.down == pressed) return;
		if (!update.event)
			update.down = pressed;
		
		if (screen != null)
			update.event = true;
		
		update.x = x;
		update.y = y;
	}
}
