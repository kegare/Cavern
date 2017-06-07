package cavern.magic;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public interface IMagic
{
	public int getMagicLevel();

	public long getMagicSpellTime();

	public double getMagicRange();

	public int getCostMP();

	public int getMagicPoint();

	public default long getMagicSpellTime(EntityPlayer player)
	{
		return getMagicSpellTime();
	}

	public default double getMagicRange(EntityPlayer player)
	{
		return getMagicRange();
	}

	public default int getCostMP(EntityPlayer player)
	{
		return getCostMP();
	}

	public default int getMagicPoint(EntityPlayer player)
	{
		return getMagicPoint();
	}

	@Nullable
	public default SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS;
	}

	@Nullable
	public default ITextComponent getFailedMessage()
	{
		return new TextComponentTranslation("cavern.magicianstats.magic.short");
	}

	public interface IEntityMagic extends IMagic
	{
		public boolean isTarget(EntityPlayer player, Entity targetEntity);

		public boolean execute(EntityPlayer player, Entity targetEntity);
	}

	public interface IPlayerMagic extends IMagic
	{
		public boolean isTarget(EntityPlayer player, EntityPlayer targetPlayer);

		public boolean execute(EntityPlayer player, EntityPlayer targetPlayer);
	}

	public interface IPlainMagic extends IMagic
	{
		public boolean execute(EntityPlayer player);
	}
}