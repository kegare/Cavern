package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.world.DimensionType;

public interface IDimension
{
	public DimensionType getCavernDimension();

	@Nullable
	public DimensionType getAquaCavernDimension();

	@Nullable
	public DimensionType getCavelandDimension();

	@Nullable
	public DimensionType getIceCavernDimension();

	@Nullable
	public DimensionType getRuinsCavernDimension();

	@Nullable
	public DimensionType getCaveniaDimension();

	public boolean isAquaCavernDisabled();

	public boolean isCavelandDisabled();

	public boolean isIceCavernDisabled();

	public boolean isRuinsCavernDisabled();

	public boolean isCaveniaDisabled();

	public boolean isEntityInCavern(@Nullable Entity entity);

	public boolean isEntityInAquaCavern(@Nullable Entity entity);

	public boolean isEntityInCaveland(@Nullable Entity entity);

	public boolean isEntityInIceCavern(@Nullable Entity entity);

	public boolean isEntityInRuinsCavern(@Nullable Entity entity);

	public boolean isEntityInCavenia(@Nullable Entity entity);

	public boolean isEntityInCaves(@Nullable Entity entity);

	public boolean isCavern(@Nullable DimensionType type);

	public boolean isAquaCavern(@Nullable DimensionType type);

	public boolean isCaveland(@Nullable DimensionType type);

	public boolean isIceCavern(@Nullable DimensionType type);

	public boolean isRuinsCavern(@Nullable DimensionType type);

	public boolean isCavenia(@Nullable DimensionType type);

	public boolean isCaves(@Nullable DimensionType type);
}