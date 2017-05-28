package cavern.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public String translate(String key)
	{
		return I18n.translateToLocal(key);
	}

	public String translateFormat(String key, Object... format)
	{
		return I18n.translateToLocalFormatted(key, format);
	}

	public boolean isSinglePlayer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();
	}

	public boolean hasAchievementClient(EntityPlayer player, Achievement achievement)
	{
		return false;
	}
}