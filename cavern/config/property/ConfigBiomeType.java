package cavern.config.property;

import net.minecraft.util.math.MathHelper;

public class ConfigBiomeType
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
		NATURAL,
		SQUARE,
		LARGE_SQUARE
	}
}