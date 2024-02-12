package tfc.btvr.menu;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.generate.chunk.empty.ChunkDecoratorEmpty;

public class ChunkGeneratorMenu extends ChunkGenerator {
	public ChunkGeneratorMenu(World world) {
		super(world, new ChunkDecoratorEmpty());
	}

//	protected short[] doBlockGeneration(Chunk chunk) {
//		short[] blocks = new short[256 * this.world.getHeightBlocks()];
//		return blocks;
//	}
	
	@Override
	protected ChunkGeneratorResult doBlockGeneration(Chunk chunk) {
		return new ChunkGeneratorResult();
	}
}
