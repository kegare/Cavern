package cavern.handler.api;

import cavern.api.IDimension;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import net.minecraft.entity.Entity;

public class DimensionHandler implements IDimension
{
	@Override
	public int getCavernDimension()
	{
		return CavernConfig.dimensionId;
	}

	@Override
	public int getAquaCavernDimension()
	{
		return AquaCavernConfig.dimensionId;
	}

	@Override
	public int getCavelandDimension()
	{
		return CavelandConfig.dimensionId;
	}

	@Override
	public boolean isEntityInCavern(Entity entity)
	{
		return entity != null && entity.dimension == getCavernDimension();
	}

	@Override
	public boolean isEntityInAquaCavern(Entity entity)
	{
		return entity != null && entity.dimension == getAquaCavernDimension();
	}

	@Override
	public boolean isEntityInCaveland(Entity entity)
	{
		return entity != null && entity.dimension == getCavelandDimension();
	}

	@Override
	public boolean isEntityInCaves(Entity entity)
	{
		return isEntityInCavern(entity) || isEntityInAquaCavern(entity) || isEntityInCaveland(entity);
	}
}