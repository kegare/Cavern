package cavern.magic;

import cavern.core.Cavern;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicStorage implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicStorage(int level, long time)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		return magicSpellTime;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 15 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return MagicianStats.get(player).getRank() > MagicianRank.NOVICE_MAGICIAN.getRank() ? 0 : 1;
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return SoundEvents.UI_BUTTON_CLICK;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		player.openGui(Cavern.instance, 0, player.world, hand == EnumHand.MAIN_HAND ? 0 : 1, 2 + getMagicLevel(), 0);

		return true;
	}
}