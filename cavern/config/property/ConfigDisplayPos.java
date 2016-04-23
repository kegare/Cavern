package cavern.config.property;

import net.minecraft.util.math.MathHelper;

public class ConfigDisplayPos
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
		TOP_RIGHT,
		TOP_LEFT,
		BOTTOM_RIGHT,
		BOTTOM_LEFT,
		HIDDEN;

		public boolean isTop()
		{
			return this == TOP_RIGHT || this == TOP_LEFT;
		}

		public boolean isBottom()
		{
			return this == BOTTOM_RIGHT || this == BOTTOM_LEFT;
		}

		public boolean isRight()
		{
			return this == TOP_RIGHT || this == BOTTOM_RIGHT;
		}

		public boolean isLeft()
		{
			return this == TOP_LEFT || this == BOTTOM_LEFT;
		}

		public boolean isHidden()
		{
			return this == HIDDEN;
		}
	}
}