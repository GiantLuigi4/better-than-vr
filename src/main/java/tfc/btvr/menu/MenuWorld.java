package tfc.btvr.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.SaveHandlerClientMP;
import net.minecraft.core.world.type.WorldTypeEmpty;
import org.lwjgl.opengl.GL11;

public class MenuWorld {
	World dummy = new World(
			new SaveHandlerClientMP(), "renderWorld",
			0, Dimension.overworld, new WorldTypeEmpty("empty.lol")
	);
	RenderBlocks blocks = new RenderBlocks(dummy, dummy);
	
	public static MenuWorld select() {
		MenuWorld wrld = new MenuWorld();
		
		for (int x = -30; x <= 30; x++) {
			for (int z = -30; z <= 30; z++) {
				wrld.dummy.setBlock(x, -1, z, Block.grass.id);
			}
		}
		
		return wrld;
	}
	
	int list = 0;
	
	public void draw(float renderPartialTicks, Minecraft mc) {
		BlockModelRenderBlocks.setRenderBlocks(blocks);
		BlockModel model = BlockModelDispatcher.getInstance().getDispatch(Block.grass);
		Tessellator tessellator = Tessellator.instance;
		
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/terrain.png"));
		Lighting.disable();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glDisable(2884);
		if (mc.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(7425);
		} else {
			GL11.glShadeModel(7424);
		}
		
		if (list == 0) {
			list = GL11.glGenLists(1);
			GL11.glNewList(list, 4864);
			tessellator.startDrawingQuads();
			tessellator.setTranslation(0, 0, 0);
			tessellator.setColorOpaque(1, 1, 1);
			
			for (int x = -30; x <= 30; x++) {
				for (int z = -30; z <= 30; z++) {
					model.render(Block.grass, x, -1, z);
				}
			}
			
			tessellator.setTranslation(0.0, 0.0, 0.0);
			tessellator.draw();
			GL11.glEndList();
		} else {
			GL11.glCallList(list);
		}
		Lighting.enableLight();
	}
}
