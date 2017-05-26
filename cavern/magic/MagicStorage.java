package cavern.magic;

import cavern.core.Cavern;
import cavern.magic.IMagic.IPlainMagic;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public class MagicStorage implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final ItemStack storageItem;

	public MagicStorage(int level, long time, ItemStack stack)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.storageItem = stack;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@Override
	public long getMagicSpellTime()
	{
		return magicSpellTime;
	}

	@Override
	public double getMagicRange()
	{
		return 0.0F;
	}

	@Override
	public int getCostMP()
	{
		return 15 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return 1;
	}

	@Override
	public int getMagicPoint(EntityPlayer player)
	{
		return MagicianStats.get(player).getRank() > MagicianRank.NOVICE_MAGICIAN.getRank() ? 0 : getMagicPoint();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return SoundEvents.UI_BUTTON_CLICK;
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		int index = -1;
		ItemStack held = player.getHeldItemMainhand();

		if (ItemStack.areItemStacksEqual(held, storageItem))
		{
			index = 0;
		}
		else
		{
			held = player.getHeldItemOffhand();

			if (ItemStack.areItemStacksEqual(held, storageItem))
			{
				index = 1;
			}
		}

		if (index < 0)
		{
			return false;
		}

		player.openGui(Cavern.instance, 0, player.world, index, 2 + getMagicLevel(), 0);

		return true;
	}
}