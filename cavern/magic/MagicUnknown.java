package cavern.magic;

import java.util.Random;

import cavern.core.CaveSounds;
import cavern.item.ItemMagicalBook;
import cavern.item.ItemMagicalBook.EnumType;
import cavern.magic.IMagic.IPlainMagic;
import cavern.stats.MagicianStats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicUnknown implements IPlainMagic
{
	private static final Random RANDOM = new Random();

	private final int magicLevel;
	private final long magicSpellTime;
	private final ItemStack magicalBook;

	public MagicUnknown(int level, long time, ItemStack book)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.magicalBook = book;
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
		return 0.0D;
	}

	@Override
	public int getCostMP()
	{
		return 50 * Math.max(getMagicLevel(), 1);
	}

	@Override
	public int getMagicPoint()
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
	public boolean execute(EntityPlayer player)
	{
		EnumType[] books = EnumType.values();
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
			magicalBook.shrink(1);
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

		World world = player.world;
		EntityItem drop = new EntityItem(world, player.posX, player.posY + 0.5D, player.posZ, bookItem);

		drop.setPickupDelay(15);

		world.spawnEntity(drop);

		return true;
	}
}