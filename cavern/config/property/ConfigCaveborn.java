package cavern.config.property;

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

	public void setValue(int pos)
	{
		value = pos;
	}

	public Type getType()
	{
		Type[] types = Type.values();
		int max = types.length - 1;

		return types[MathHelper.clamp_int(getValue(), 0, max)];
	}

	public enum Type
	{
		DISABLED(null),
		CAVERN(CaveBlocks.cavern_portal),
		AQUA_CAVERN(CaveBlocks.aqua_cavern_portal),
		CAVELAND(CaveBlocks.caveland_portal);

		private final BlockPortalCavern portalBlock;

		private Type(BlockPortalCavern block)
		{
			this.portalBlock = block;
		}

		public BlockPortalCavern getPortalBlock()
		{
			return portalBlock;
		}
	}
}