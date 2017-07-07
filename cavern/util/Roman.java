package cavern.util;

import org.apache.commons.lang3.StringUtils;

public class Roman
{
	private static final int[] VALUES = {1, 10, 100, 1000};
	private static final char[] ONES = {'I', 'X', 'C', 'M'};
	private static final char[] FIVES = {'V', 'L', 'D'};

	public static String toRoman(int num)
	{
		if (num <= 0 || num >= 4000)
		{
			return "";
		}

		StringBuilder ret = new StringBuilder();

		for (int i = 3; i >= 0; --i)
		{
			int r = num / VALUES[i];
			num %= VALUES[i];

			if (r == 4)
			{
				ret.append(ONES[i]).append(FIVES[i]);
				continue;
			}

			if (r == 9)
			{
				ret.append(ONES[i]).append(ONES[i + 1]);
				continue;
			}

			if (r >= 5)
			{
				ret.append(FIVES[i]);
				r -= 5;
			}

			ret.append(StringUtils.repeat(ONES[i], r));
		}

		return ret.toString();
	}
}