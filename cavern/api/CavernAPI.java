package cavern.api;

import net.minecraft.entity.player.EntityPlayer;

public final class CavernAPI
{
	public static final String API_VERSION = "1.2.2";

	public static ICavernAPI apiHandler;
	public static IDimension dimension;

	public static IMinerStats getMinerStats(EntityPlayer player)
	{
		if (apiHandler != null)
		{
			return apiHandler.getMinerStats(player);
		}

		return null;
	}
}