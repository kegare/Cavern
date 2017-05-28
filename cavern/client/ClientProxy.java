package cavern.client;

import cavern.core.CommonProxy;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public String translate(String key)
	{
		return I18n.format(key);
	}

	@Override
	public String translateFormat(String key, Object... format)
	{
		return I18n.format(key, format);
	}

	@Override
	public boolean isSinglePlayer()
	{
		return FMLClientHandler.instance().getClient().isSingleplayer();
	}

	@Override
	public boolean hasAchievementClient(EntityPlayer player, Achievement achievement)
	{
		if (player != null && player instanceof EntityPlayerSP)
		{
			return ((EntityPlayerSP)player).getStatFileWriter().hasAchievementUnlocked(achievement);
		}

		return false;
	}
}