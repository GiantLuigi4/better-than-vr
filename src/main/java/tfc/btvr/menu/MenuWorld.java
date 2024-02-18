package tfc.btvr.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderGlobal;
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
import tfc.btvr.util.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class MenuWorld {
	public final World dummy = new World(
			new MenuWorldSaveHandler(), "renderWorld",
			0, Dimension.overworld, new WorldTypeMenu("empty.lol")
	);
	public final RenderBlocks blocks = new RenderBlocks(dummy, dummy);
	public final EntityPlayer myPlayer;
	
	public MenuWorld(Minecraft mc) {
		myPlayer = new EntityPlayerSP(mc, dummy, mc.session, 0);
		dummy.entityJoinedWorld(myPlayer);
		dummy.setWorldTime(0);
	}
	
	protected static int readInt(InputStream is) throws IOException {
		return
				(is.read() << 24) |
						(is.read() << 16) |
						(is.read() << 8) |
						(is.read());
	}
	
	public int sz;
	
	protected static String chooseRow() throws IOException {
		InputStream is = MenuWorld.class.getClassLoader().getResourceAsStream("btvr/menu/worlds.csv");
		byte[] data = is.readAllBytes();
		try {
			is.close();
		} catch (Throwable err) {
		}
		String dat = new String(data);
		String[] lns = dat.split("\n");
		
		ArrayList<String> lines = new ArrayList<>();
		for (String ln : lns) {
			ln = ln.trim();
			if (ln.isEmpty() || ln.startsWith("#")) continue;
			
			lines.add(ln);
		}
		
		double v = Math.random() * (lines.size());
		int i = (int) v;
		String ln = lines.get(i);
		
		String[] splat = ln.split(",");
		// TODO: return a world info instead of a string
		return splat[2].trim();
	}
	
	public static MenuWorld select(Minecraft mc) {
		MenuWorld wrld = new MenuWorld(mc);
		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				wrld.dummy.getChunkProvider().prepareChunk(x, z);
				wrld.dummy.getChunkProvider().populate(wrld.dummy.getChunkProvider(), x, z);
			}
		}
		
		switch (Config.MENU_MODE.get()) {
			case FLAT:
				wrld.sz = 30;
				for (int x = -30; x <= 30; x++) {
					for (int z = -30; z <= 30; z++) {
						wrld.dummy.setBlock(x, 28, z, Block.grass.id);
					}
				}
				wrld.myPlayer.setPos(0.5f, 28 + 2 + wrld.myPlayer.heightOffset - 0.99, 0.5f);
				break;
			case VOID:
				wrld.sz = 32;
				wrld.myPlayer.setPos(0.5f, 30 + 2 + wrld.myPlayer.heightOffset - 0.99, 0.5f);
				break;
			default:
			case RANDOM:
				try {
					String txt = chooseRow();
					
					InputStream fis = MenuWorld.class.getClassLoader().getResourceAsStream("btvr/menu/" + txt);
					GZIPInputStream gzis = new GZIPInputStream(fis);
					
					int size = readInt(gzis);
					wrld.sz = size;
					
					for (int x = -size; x <= size; x++) {
						for (int y = -size; y <= size; y++) {
							for (int z = -size; z <= size; z++) {
								int id = readInt(gzis);
								int meta = (byte) gzis.read();
								wrld.dummy.setBlock(x, y + size, z, id);
								wrld.dummy.setBlockMetadata(x, y + size, z, meta);
							}
						}
					}
					
					wrld.myPlayer.setPos(0.5f, size + wrld.myPlayer.heightOffset - 0.99, 0.5f);
					
					gzis.close();
					try {
						fis.close();
					} catch (Throwable err) {
					}
				} catch (Throwable err) {
				}
				
				break;
		}
		
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				wrld.dummy.setBlock(x, wrld.sz - 2, y, Block.slabStonePolished.id);
				wrld.dummy.setBlockMetadata(x, wrld.sz - 2, y, 1);
				
				wrld.dummy.setBlock(x, wrld.sz - 1, y, 0);
				wrld.dummy.setBlockMetadata(x, wrld.sz - 1, y, 0);
				wrld.dummy.setBlock(x, wrld.sz, y, 0);
				wrld.dummy.setBlockMetadata(x, wrld.sz, y, 0);
			}
		}
		
		for (int x = -wrld.sz; x <= wrld.sz; x++) {
			for (int y = -wrld.sz; y <= wrld.sz; y++) {
				for (int z = -wrld.sz; z <= wrld.sz; z++) {
					Block id = wrld.dummy.getBlock(x, y + wrld.sz, z);
					if (id == null || !id.blocksLight()) {
						wrld.dummy.setLightValue(LightLayer.Block, x, y + wrld.sz, z, 15);
						wrld.dummy.setLightValue(LightLayer.Sky, x, y + wrld.sz, z, 15);
					}
				}
			}
		}
		
		RenderGlobal renderglobal = mc.renderGlobal;
		renderglobal.changeWorld(wrld.dummy);
		
		return wrld;
	}
	
	int list = 0;
	int list1 = 0;
	
	public void draw(float renderPartialTicks, Minecraft mc) {
		BlockModelRenderBlocks.setRenderBlocks(blocks);
		Tessellator tessellator = Tessellator.instance;
		
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/terrain.png"));
		Lighting.disable();
		GL11.glDisable(2884);
		if (mc.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(7425);
		} else {
			GL11.glShadeModel(7424);
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		if (list == 0) {
			// pass 1
			list = GL11.glGenLists(1);
			GL11.glNewList(list, 4864);
			tessellator.startDrawingQuads();
//			tessellator.setTranslation(-0.5f, -sz + 1, -0.5f);
			tessellator.setColorOpaque(1, 1, 1);
			
			for (int x = -sz; x <= sz; x++) {
				for (int y = -sz; y <= sz; y++) {
					for (int z = -sz; z <= sz; z++) {
						int id = dummy.getBlockId(x, y + sz, z);
						if (id == 0) continue;
						
						Block blk = Block.getBlock(id);
						if (blk.getRenderBlockPass() == 0) {
							BlockModel model = BlockModelDispatcher.getInstance().getDispatch(blk);
							model.render(blk, x, y + sz, z);
						}
					}
				}
			}
			
			tessellator.setTranslation(0.0, 0.0, 0.0);
			tessellator.draw();
			GL11.glEndList();
			
			// pass 2
			list1 = GL11.glGenLists(1);
			GL11.glNewList(list1, 4864);
			tessellator.startDrawingQuads();
//			tessellator.setTranslation(-0.5f, -sz + 1, -0.5f);
			tessellator.setColorOpaque(1, 1, 1);
			
			for (int x = -sz; x <= sz; x++) {
				for (int y = -sz; y <= sz; y++) {
					for (int z = -sz; z <= sz; z++) {
						int id = dummy.getBlockId(x, y + sz, z);
						if (id == 0) continue;
						
						Block blk = Block.getBlock(id);
						if (blk.getRenderBlockPass() == 1) {
							BlockModel model = BlockModelDispatcher.getInstance().getDispatch(blk);
							model.render(blk, x, y + sz, z);
						}
					}
				}
			}
			
			tessellator.setTranslation(0.0, 0.0, 0.0);
			tessellator.draw();
			GL11.glEndList();
		}
		
		GL11.glDisable(3042);
		GL11.glCallList(list);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glCallList(list1);
		GL11.glDisable(3042);
		
		Lighting.enableLight();
	}
	
	public void delete() {
		GL11.glDeleteLists(list, 1);
	}
}
