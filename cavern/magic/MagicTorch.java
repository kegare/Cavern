package cavern.magic;

import cavern.entity.EntityMagicTorcher;
import cavern.magic.IMagic.IPlainMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class MagicTorch implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	private int errorCode;

	public MagicTorch(int level, long time, double range)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.magicRange = range;
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
	public long getMagicSpellTime(EntityPlayer player)
	{
		return player.isSneaking() ? getMagicSpellTime() : getMagicSpellTime() / 2;
	}

	@Override
	public double getMagicRange()
	{
		return magicRange;
	}

	@Override
	public double getMagicRange(EntityPlayer player)
	{
		return player.isSneaking() ? getMagicRange() : 6.0D;
	}

	@Override
	public int getCostMP()
	{
		return 10 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return getMagicLevel();
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		switch (errorCode)
		{
			case 1:
				return new TextComponentTranslation("item.magicalBook.torch.lack");
			case 2:
				return new TextComponentTranslation("item.magicalBook.torch.exist");
		}

		return IPlainMagic.super.getFailedMessage();
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		if (!player.inventory.hasItemStack(new ItemStack(Blocks.TORCH)))
		{
			errorCode = 1;

			return false;
		}

		World world = player.world;
		double range = getMagicRange(player);

		for (EntityMagicTorcher torcher : world.getEntitiesWithinAABB(EntityMagicTorcher.class, player.getEntityBoundingBox().expand(range, 5.0D, range), EntitySelectors.IS_ALIVE))
		{
			if (torcher.getPlayer() == player)
			{
				errorCode = 2;

				return false;
			}
		}

		EntityMagicTorcher torcher = new EntityMagicTorcher(world, player, MathHelper.ceil(range));

		torcher.setLifeTime(60 * getMagicLevel() * 20);
		torcher.setTracking(!player.isSneaking());

		world.spawnEntity(torcher);

		errorCode = 0;

		return true;
	}
}