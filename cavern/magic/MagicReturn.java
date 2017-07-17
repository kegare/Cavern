package cavern.magic;

import cavern.core.CaveSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicReturn implements IEntityMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	private int errorCode;

	public MagicReturn(int level, long time, double range)
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

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		return magicSpellTime;
	}

	@Override
	public double getMagicRange(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return magicRange;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 100 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 5 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public boolean isTargetEntity(EntityPlayer player, Entity entity)
	{
		if (player.isEntityEqual(entity))
		{
			return true;
		}

		if (getMagicLevel() <= 1 || !player.canEntityBeSeen(entity))
		{
			return false;
		}

		if (entity instanceof EntityPlayer)
		{
			return true;
		}

		if (entity instanceof IEntityOwnable)
		{
			IEntityOwnable ownable = (IEntityOwnable)entity;

			if (player.isEntityEqual(ownable.getOwner()) || player.getCachedUniqueIdString().equals(ownable.getOwnerId().toString()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		switch (errorCode)
		{
			case 1:
				return new TextComponentTranslation("item.magicalBook.return.no");
		}

		return IEntityMagic.super.getFailedMessage();
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand)
	{
		EntityLivingBase target = (EntityLivingBase)entity;
		BlockPos spawnPos = player.getBedLocation();

		if (spawnPos == null)
		{
			errorCode = 1;

			return false;
		}

		if (target.attemptTeleport(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D))
		{
			return true;
		}

		errorCode = 0;

		return false;
	}
}