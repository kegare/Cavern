package cavern.api;

import net.minecraft.entity.Entity;

public interface IDimension
{
	public int getCavernDimension();

	public int getAquaCavernDimension();

	public int getCavelandDimension();

	public boolean isEntityInCavern(Entity entity);

	public boolean isEntityInAquaCavern(Entity entity);

	public boolean isEntityInCaveland(Entity entity);

	public boolean isEntityInCaves(Entity entity);
}