package tfc.btvr.menu;

import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldTypeOverworld;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.wind.WindManagerGeneric;

public class WorldTypeMenu extends WorldTypeOverworld {
	public WorldTypeMenu(String languageKey) {
		super(languageKey, Weather.overworldClear, new WindManagerGeneric(), SeasonConfig.builder().withSingleSeason(Seasons.OVERWORLD_SUMMER).build());
	}
	
	public int getMinY() {
		return 0;
	}
	
	public int getMaxY() {
		return 31;
	}
	
	public int getOceanY() {
		return 0;
	}
	
	public BiomeProvider createBiomeProvider(World world) {
		return new BiomeProviderSingleBiome(Biomes.OVERWORLD_PLAINS, 0.5, 0.0, 0.0);
	}
	
	public ChunkGenerator createChunkGenerator(World world) {
		return new ChunkGeneratorMenu(world);
	}
	
	public boolean isValidSpawn(World world, int x, int y, int z) {
		return true;
	}
	
	public void getRespawnLocation(World world) {
	}
	
	public float getCelestialAngle(World world, long tick, float partialTick) {
		return 0.125F;
	}
	
	public float getCloudHeight() {
		return 236.0F;
	}
	
	public boolean hasAurora() {
		return false;
	}
}
