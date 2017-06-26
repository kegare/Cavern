package cavern.handler.api;

import cavern.api.IDimension;
import cavern.world.CaveType;
import net.minecraft.entity.Entity;
import net.minecraft.world.DimensionType;

public class DimensionHandler implements IDimension
{
	@Override
	public DimensionType getCavernDimension()
	{
		return CaveType.DIM_CAVERN;
	}

	@Override
	public DimensionType getAquaCavernDimension()
	{
		return CaveType.DIM_AQUA_CAVERN;
	}

	@Override
	public DimensionType getCavelandDimension()
	{
		return CaveType.DIM_CAVELAND;
	}

	@Override
	public DimensionType getIceCavernDimension()
	{
		return CaveType.DIM_ICE_CAVERN;
	}

	@Override
	public DimensionType getRuinsCavernDimension()
	{
		return CaveType.DIM_RUINS_CAVERN;
	}

	@Override
	public DimensionType getCaveniaDimension()
	{
		return CaveType.DIM_CAVENIA;
	}

	@Override
	public boolean isAquaCavernDisabled()
	{
		return getAquaCavernDimension() == null;
	}

	@Override
	public boolean isCavelandDisabled()
	{
		return getCavelandDimension() == null;
	}

	@Override
	public boolean isIceCavernDisabled()
	{
		return getIceCavernDimension() == null;
	}

	@Override
	public boolean isRuinsCavernDisabled()
	{
		return getRuinsCavernDimension() == null;
	}

	@Override
	public boolean isCaveniaDisabled()
	{
		return getCaveniaDimension() == null;
	}

	@Override
	public boolean isEntityInCavern(Entity entity)
	{
		if (entity == null)
		{
			return false;
		}

		return entity.dimension == getCavernDimension().getId();
	}

	@Override
	public boolean isEntityInAquaCavern(Entity entity)
	{
		if (entity == null || isAquaCavernDisabled())
		{
			return false;
		}

		return entity.dimension == getAquaCavernDimension().getId();
	}

	@Override
	public boolean isEntityInCaveland(Entity entity)
	{
		if (entity == null || isCavelandDisabled())
		{
			return false;
		}

		return entity.dimension == getCavelandDimension().getId();
	}

	@Override
	public boolean isEntityInIceCavern(Entity entity)
	{
		if (entity == null || isIceCavernDisabled())
		{
			return false;
		}

		return entity.dimension == getIceCavernDimension().getId();
	}

	@Override
	public boolean isEntityInRuinsCavern(Entity entity)
	{
		if (entity == null || isRuinsCavernDisabled())
		{
			return false;
		}

		return entity.dimension == getRuinsCavernDimension().getId();
	}

	@Override
	public boolean isEntityInCavenia(Entity entity)
	{
		if (entity == null || isCaveniaDisabled())
		{
			return false;
		}

		return entity.dimension == getCaveniaDimension().getId();
	}

	@Override
	public boolean isEntityInCaves(Entity entity)
	{
		return isEntityInCavern(entity) || isEntityInAquaCavern(entity) || isEntityInCaveland(entity) || isEntityInIceCavern(entity) || isEntityInRuinsCavern(entity) || isEntityInCavenia(entity);
	}

	@Override
	public boolean isCavern(DimensionType type)
	{
		if (type == null)
		{
			return false;
		}

		return type == getCavernDimension();
	}

	@Override
	public boolean isAquaCavern(DimensionType type)
	{
		if (type == null || isAquaCavernDisabled())
		{
			return false;
		}

		return type == getAquaCavernDimension();
	}

	@Override
	public boolean isCaveland(DimensionType type)
	{
		if (type == null || isCavelandDisabled())
		{
			return false;
		}

		return type == getCavelandDimension();
	}

	@Override
	public boolean isIceCavern(DimensionType type)
	{
		if (type == null || isIceCavernDisabled())
		{
			return false;
		}

		return type == getIceCavernDimension();
	}

	@Override
	public boolean isRuinsCavern(DimensionType type)
	{
		if (type == null || isRuinsCavernDisabled())
		{
			return false;
		}

		return type == getRuinsCavernDimension();
	}

	@Override
	public boolean isCavenia(DimensionType type)
	{
		if (type == null || isCaveniaDisabled())
		{
			return false;
		}

		return type == getCaveniaDimension();
	}

	@Override
	public boolean isCaves(DimensionType type)
	{
		return isCavern(type) || isAquaCavern(type) || isCaveland(type) || isIceCavern(type) || isRuinsCavern(type) || isCavenia(type);
	}
}