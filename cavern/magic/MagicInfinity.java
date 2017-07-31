package cavern.magic;

import cavern.api.IMagicianStats;
import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicInfinity implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicInfinity(int level, long time)
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
		return getMagicLevel() <= 3 ? 300 : 500;
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 10 * getMagicLevel();
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		IMagicianStats stats = MagicianStats.get(player);

		if (stats.getInfinity() > 0)
		{
			return false;
		}

		if (stack.hasTagCompound() && !player.capabilities.isCreativeMode)
		{
			long prevTime = stack.getTagCompound().getLong("InfinityTime");

			if (prevTime > 0 && prevTime + 6000L > world.getTotalWorldTime())
			{
				return false;
			}
		}

		stats.setInfinity(getMagicLevel(), 1200);

		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();

			stack.setTagCompound(nbt);
		}

		nbt.setLong("InfinityTime", world.getTotalWorldTime());

		return true;
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_HOLY;
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		return new TextComponentTranslation("item.magicalBook.infinity.failed");
	}
}