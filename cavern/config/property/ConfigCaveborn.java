package cavern.config.property;

import javax.annotation.Nullable;

import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;

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
		return Type.get(getValue());
	}

	public enum Type
	{
		DISABLED(0, null),
		RUINS_CAVERN(1, CaveBlocks.RUINS_CAVERN_PORTAL),
		CAVERN(2, CaveBlocks.CAVERN_PORTAL),
		AQUA_CAVERN(3, CaveBlocks.AQUA_CAVERN_PORTAL),
		CAVELAND(4, CaveBlocks.CAVELAND_PORTAL),
		ICE_CAVERN(5, CaveBlocks.ICE_CAVERN_PORTAL),
		HUGE_CAVERN(6, CaveBlocks.HUGE_CAVERN_PORTAL);

		public static final Type[] VALUES = new Type[values().length];

		private final int type;
		private final BlockPortalCavern portalBlock;

		private Type(int type, BlockPortalCavern block)
		{
			this.type = type;
			this.portalBlock = block;
		}

		public int getType()
		{
			return type;
		}

		@Nullable
		public BlockPortalCavern getPortalBlock()
		{
			return portalBlock;
		}

		public static Type get(int type)
		{
			if (type < 0 || type >= VALUES.length)
			{
				type = 0;
			}

			return VALUES[type];
		}

		static
		{
			for (Type type : values())
			{
				VALUES[type.getType()] = type;
			}
		}
	}
}