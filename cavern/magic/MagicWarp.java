package cavern.magic;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import cavern.magic.IMagic.IPlainMagic;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;

public class MagicWarp implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final ItemStack magicalBook;

	public MagicWarp(int level, long time, ItemStack book)
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
		return hasWarpPoint() ? magicSpellTime : magicSpellTime / 2;
	}

	@Override
	public double getMagicRange()
	{
		switch (getMagicLevel())
		{
			case 1:
				return 100.0D;
			case 2:
				return 300.0D;
			case 3:
				return 500.0D;
		}

		return Double.MAX_VALUE;
	}

	@Override
	public int getCostMP()
	{
		return (hasWarpPoint() ? 100 : 50) * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return 2 * getMagicLevel();
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		return new TextComponentTranslation("item.magicalBook.warp.point.far");
	}

	public boolean hasWarpPoint()
	{
		return hasWarpPoint(magicalBook);
	}

	@Nullable
	public Pair<BlockPos, Integer> getWarpPoint()
	{
		return getWarpPoint(magicalBook);
	}

	public void setWarpPoint(@Nullable BlockPos pos, int dim)
	{
		setWarpPoint(magicalBook, pos, dim);
	}

	public static boolean hasWarpPoint(ItemStack magicalBook)
	{
		NBTTagCompound nbt = magicalBook.getTagCompound();

		if (nbt == null)
		{
			return false;
		}

		return nbt.hasKey("WarpPoint", NBT.TAG_COMPOUND);
	}

	@Nullable
	public static Pair<BlockPos, Integer> getWarpPoint(ItemStack magicalBook)
	{
		if (!hasWarpPoint(magicalBook))
		{
			return null;
		}

		NBTTagCompound nbt = magicalBook.getTagCompound();
		NBTTagCompound compound = nbt.getCompoundTag("WarpPoint");
		BlockPos pos = NBTUtil.getPosFromTag(compound);
		int dim = compound.getInteger("Dim");

		return Pair.of(pos, Integer.valueOf(dim));
	}

	public static void setWarpPoint(ItemStack magicalBook, @Nullable BlockPos pos, int dim)
	{
		NBTTagCompound nbt = magicalBook.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		if (pos == null)
		{
			nbt.removeTag("WarpPoint");
		}
		else
		{
			NBTTagCompound compound = new NBTTagCompound();

			compound.setInteger("X", pos.getX());
			compound.setInteger("Y", pos.getY());
			compound.setInteger("Z", pos.getZ());
			compound.setInteger("Dim", dim);

			nbt.setTag("WarpPoint", compound);
		}

		magicalBook.setTagCompound(nbt);
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		if (hasWarpPoint())
		{
			Pair<BlockPos, Integer> warpPoint = getWarpPoint();
			BlockPos pos = warpPoint.getLeft();
			int dim = warpPoint.getRight().intValue();

			if (player.dimension != dim)
			{
				return false;
			}

			if (Math.sqrt(player.getDistanceSqToCenter(pos)) > getMagicRange(player))
			{
				return false;
			}

			CaveUtils.setLocationAndAngles(player, pos);

			setWarpPoint(null, player.dimension);

			CaveUtils.grantAdvancement(player, "magic_warp");
		}
		else
		{
			setWarpPoint(player.getPosition(), player.dimension);

			player.sendStatusMessage(new TextComponentTranslation("item.magicalBook.warp.point.set"), true);
		}

		return true;
	}
}