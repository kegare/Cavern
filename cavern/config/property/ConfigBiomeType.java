package cavern.config.property;

import net.minecraft.util.math.MathHelper;

public class ConfigBiomeType
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
		NATURAL,
		SQUARE,
		LARGE_SQUARE
	}
}