package cavern.magic;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import cavern.core.CaveSounds;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicWarp implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicWarp(int level, long time)
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
		return hasWarpPoint(stack) ? magicSpellTime : magicSpellTime / 2;
	}

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
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return (hasWarpPoint(stack) ? 100 : 50) * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 2 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		return new TextComponentTranslation("item.magicalBook.warp.point.far");
	}

	public static boolean hasWarpPoint(ItemStack magicalBook)
	{
		NBTTagCompound nbt = magicalBook.getTagCompound();

		return nbt != null && nbt.hasKey("WarpPoint", NBT.TAG_COMPOUND);
	}

	@Nullable
	public static Pair<BlockPos, DimensionType> getWarpPoint(ItemStack magicalBook)
	{
		if (!hasWarpPoint(magicalBook))
		{
			return null;
		}

		NBTTagCompound nbt = magicalBook.getTagCompound();
		NBTTagCompound compound = nbt.getCompoundTag("WarpPoint");
		BlockPos pos = NBTUtil.getPosFromTag(compound);
		DimensionType type;

		try
		{
			type = DimensionType.getById(compound.getInteger("Dim"));
		}
		catch (IllegalArgumentException e)
		{
			nbt.removeTag("WarpPoint");

			return null;
		}

		return Pair.of(pos, type);
	}

	public static void setWarpPoint(ItemStack magicalBook, @Nullable BlockPos pos, DimensionType type)
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
			NBTTagCompound compound = NBTUtil.createPosTag(pos);
			compound.setInteger("Dim", type.getId());

			nbt.setTag("WarpPoint", compound);
		}

		magicalBook.setTagCompound(nbt);
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		DimensionType type = player.world.provider.getDimensionType();
		Pair<BlockPos, DimensionType> warpPoint = getWarpPoint(stack);

		if (warpPoint != null)
		{
			BlockPos pos = warpPoint.getLeft();

			if (type != warpPoint.getRight())
			{
				return false;
			}

			if (Math.sqrt(player.getDistanceSqToCenter(pos)) > getMagicRange())
			{
				return false;
			}

			if (!player.attemptTeleport(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D))
			{
				return false;
			}

			setWarpPoint(stack, null, type);

			CaveUtils.grantAdvancement(player, "magic_warp");
		}
		else
		{
			setWarpPoint(stack, player.getPosition(), type);

			player.sendStatusMessage(new TextComponentTranslation("item.magicalBook.warp.point.set"), true);
		}

		return true;
	}
}