package cavern.client;

import javax.annotation.Nullable;

import cavern.core.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
	public float getBlockReachDistance(@Nullable EntityPlayer player)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null)
		{
			return mc.playerController.getBlockReachDistance();
		}

		return 0.0F;
	}
}