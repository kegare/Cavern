package cavern.client.gui;

import java.util.Collection;

import net.minecraft.world.biome.BiomeGenBase;

public interface IBiomeSelector
{
	public void onBiomeSelected(int id, Collection<BiomeGenBase> selected);

	public boolean canSelectBiome(int id, BiomeGenBase biome);
}