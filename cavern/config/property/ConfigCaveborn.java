package cavern.config.property;

import javax.annotation.Nullable;

import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;
import net.minecraft.util.math.MathHelper;

public class ConfigCaveborn
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int type)
	{
		value = type;
	}

	public Type getType()
	{
		Type[] types = Type.values();
		int max = types.length - 1;

		return types[MathHelper.clamp(getValue(), 0, max)];
	}

	public enum Type
	{
		DISABLED(null),
		RUINS_CAVERN(CaveBlocks.CAVERN_PORTAL),
		CAVERN(CaveBlocks.CAVERN_PORTAL),
		AQUA_CAVERN(CaveBlocks.AQUA_CAVERN_PORTAL),
		CAVELAND(CaveBlocks.CAVELAND_PORTAL),
		ICE_CAVERN(CaveBlocks.ICE_CAVERN_PORTAL);

		private final BlockPortalCavern portalBlock;

		private Type(BlockPortalCavern block)
		{
			this.portalBlock = block;
		}

		@Nullable
		public BlockPortalCavern getPortalBlock()
		{
			return portalBlock;
		}
	}
}