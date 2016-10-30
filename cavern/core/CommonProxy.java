package cavern.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.text.translation.I18n;

public class CommonProxy
{
	public void initConfigEntries() {}

	public void registerRenderers() {}

	public String translate(String key)
	{
		return I18n.translateToLocal(key);
	}

	public String translateFormat(String key, Object... format)
	{
		return I18n.translateToLocalFormatted(key, format);
	}

	public boolean hasAchievementUnlocked(EntityPlayer player, Achievement achievement)
	{
		if (player != null && player instanceof EntityPlayerMP)
		{
			return ((EntityPlayerMP)player).getStatFile().hasAchievementUnlocked(achievement);
		}

		return false;
	}
}