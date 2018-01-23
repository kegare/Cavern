package cavern.core;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

	public float getBlockReachDistance(@Nullable EntityPlayer player)
	{
		if (player != null && player instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)player).interactionManager.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		}

		return 0.0F;
	}
}