package cavern.magic;

import java.util.Random;

import cavern.core.CaveSounds;
import cavern.item.ItemMagicalBook;
import cavern.item.ItemMagicalBook.EnumType;
import cavern.stats.MagicianStats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicUnknown implements IMagic
{
	private static final Random RANDOM = new Random();

	private final int magicLevel;
	private final long magicSpellTime;

	public MagicUnknown(int level, long time)
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
		return 50 * Math.max(getMagicLevel(), 1);
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 3;
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		return null;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		EnumType[] books = EnumType.VALUES;
		int count = books.length - 1;
		ItemStack bookItem = null;

		for (EnumType book : books)
		{
			if (--count <= 0)
			{
				break;
			}

			double rarity = book.getMagicRarity();

			if (rarity <= 0.0D)
			{
				continue;
			}

			if (RANDOM.nextDouble() <= rarity)
			{
				int max = book.getMaxLevel();

				bookItem = max > 1 ? book.getItemStack(RANDOM.nextInt(max) + 1) : book.getItemStack();

				break;
			}
		}

		if (!player.capabilities.isCreativeMode)
		{
			stack.shrink(1);
		}

		if (bookItem == null || bookItem.isEmpty())
		{
			return false;
		}

		if (bookItem.getItem() instanceof ItemMagicalBook)
		{
			ItemMagicalBook magicalBook = (ItemMagicalBook)bookItem.getItem();
			int rank = MagicianStats.get(player).getRank();
			int level = magicalBook.getMagicLevel(bookItem);

			if (level > rank + 1)
			{
				magicalBook.setMagicLevel(bookItem, 1);
			}
			else if (RANDOM.nextDouble() <= 0.3D)
			{
				magicalBook.setMagicLevel(bookItem, --level);
			}
		}

		EntityItem drop = new EntityItem(world, player.posX, player.posY + 0.5D, player.posZ, bookItem);

		drop.setPickupDelay(15);

		world.spawnEntity(drop);

		return true;
	}
}