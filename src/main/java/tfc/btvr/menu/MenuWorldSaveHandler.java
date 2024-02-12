package tfc.btvr.menu;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.save.SaveHandlerClientMP;

import java.io.IOException;

public class MenuWorldSaveHandler extends SaveHandlerClientMP {
	BiomeProvider provider = new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 0.5, 0.0, 0.0);
	
	@Override
	public IChunkLoader getChunkLoader(Dimension dimension) {
		return new IChunkLoader() {
			@Override
			public Chunk loadChunk(World world, int i, int j) throws IOException {
				Chunk chnk = new Chunk(world, i, j);
				ChunkGeneratorMenu generatorMenu = new ChunkGeneratorMenu(world);
//				chnk.blocks = generatorMenu.doBlockGeneration(chnk);
//				chnk.data = new ChunkUnsignedByteArray(16, 16, 16);
//				chnk.skylightMap = new ChunkNibbleArray(16, 16, 16);
//				chnk.blocklightMap = new ChunkNibbleArray(16, 16, 16);
//				Arrays.fill(chnk.biome, (byte) Registries.BIOMES.getNumericIdOfItem(Registries.BIOMES.getItem("minecraft:overworld.grasslands")));
				provider.getHumidities(chnk.humidity, i, j, 0, 0);
				provider.getTemperatures(chnk.temperature, i, j, 0, 0);
				provider.getVarieties(chnk.variety, i, j, 0, 0);
				return chnk;
			}
			
			@Override
			public void saveChunk(World world, Chunk chunk) throws IOException {
			
			}
		};
	}
}
