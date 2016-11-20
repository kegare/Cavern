package cavern.config.property;

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
		return Type.get(getValue());
	}

	public enum Type
	{
		TOP_RIGHT(0),
		TOP_LEFT(1),
		BOTTOM_RIGHT(2),
		BOTTOM_LEFT(3),
		HIDDEN(4);

		private static final Type[] TYPE_LOOKUP = new Type[values().length];

		private int pos;

		private Type(int pos)
		{
			this.pos = pos;
		}

		public int getPos()
		{
			return pos;
		}

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

		public static Type get(int pos)
		{
			if (pos < 0)
			{
				pos = 0;
			}

			int max = TYPE_LOOKUP.length - 1;

			if (pos > max)
			{
				pos = max;
			}

			return TYPE_LOOKUP[pos];
		}

		static
		{
			for (Type type : values())
			{
				TYPE_LOOKUP[type.getPos()] = type;
			}
		}
	}
}