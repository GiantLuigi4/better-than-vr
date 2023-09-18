package tfc.btvr.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

public class MenuWorld {
	World dummy = new World(
			new MenuWorldSaveHandler(), "renderWorld",
			0, Dimension.overworld, new WorldTypeMenu("empty.lol")
	);
	RenderBlocks blocks = new RenderBlocks(dummy, dummy);
	public final EntityPlayer myPlayer;
	
	public MenuWorld(Minecraft mc) {
		myPlayer = new EntityPlayerSP(mc, dummy, mc.session, 0);
		dummy.entityJoinedWorld(myPlayer);
	}
	
	public static MenuWorld select(Minecraft mc) {
		MenuWorld wrld = new MenuWorld(mc);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				wrld.dummy.getChunkProvider().prepareChunk(x, z);
				wrld.dummy.getChunkProvider().populate(wrld.dummy.getChunkProvider(), x, z);
			}
		}
		
		for (int x = -30; x <= 30; x++) {
			for (int z = -30; z <= 30; z++) {
				wrld.dummy.setBlock(x, 30, z, Block.grass.id);
			}
		}
		
		for (int x = -32; x <= 32; x++) {
			for (int y = -32; y <= 32; y++) {
				for (int z = -32; z <= 32; z++) {
					int id = wrld.dummy.getBlockId(x, y + 30, z);
					if (id == 0) {
						wrld.dummy.setLightValue(LightLayer.Block, x, y, z, 15);
						wrld.dummy.setLightValue(LightLayer.Sky, x, y, z, 15);
					}
				}
			}
		}
		
		return wrld;
	}
	
	int list = 0;
	
	public void draw(float renderPartialTicks, Minecraft mc) {
		BlockModelRenderBlocks.setRenderBlocks(blocks);
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
			tessellator.setTranslation(0, -31, 0);
			tessellator.setColorOpaque(1, 1, 1);
			
			for (int x = -30; x <= 30; x++) {
				for (int y = -30; y <= 30; y++) {
					for (int z = -30; z <= 30; z++) {
						int id = dummy.getBlockId(x, y + 30, z);
						if (id == 0) continue;
						
						Block blk = Block.getBlock(id);
						BlockModel model = BlockModelDispatcher.getInstance().getDispatch(blk);
						model.render(blk, x, y + 30, z);
					}
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
