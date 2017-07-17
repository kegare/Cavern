package cavern.magic;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMagic
{
	public default boolean isClientMagic()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public default void onSpellingTick(ItemStack stack, EnumHand hand, long spellingTime, long magicSpellTime, double progress) {}

	@SideOnly(Side.CLIENT)
	public default boolean shouldCauseSpellingParticles(ItemStack stack, EnumHand hand, long spellingTime, long magicSpellTime, double progress)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public default boolean isFinishedSpelling(ItemStack stack, EnumHand hand, long spellingTime, long magicSpellTime)
	{
		return spellingTime >= magicSpellTime;
	}

	@SideOnly(Side.CLIENT)
	public default void onStopSpelling(ItemStack stack, EnumHand hand, long spellingTime, double progress) {}

	@SideOnly(Side.CLIENT)
	public default int getSpellingSpeed(int spellingSpeed)
	{
		return spellingSpeed;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public default SoundEvent getSpellingSound()
	{
		return CaveSounds.SPELLING;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public default SoundEvent getStopSpellingSound()
	{
		return CaveSounds.SPELLING_END;
	}

	@SideOnly(Side.CLIENT)
	public default float getMagicShortDamage()
	{
		return MathHelper.clamp(3 * getMagicLevel(), 1, 10);
	}

	public int getMagicLevel();

	@SideOnly(Side.CLIENT)
	public long getMagicSpellTime(ItemStack stack, EnumHand hand);

	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand);

	public default int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return getMagicLevel();
	}

	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand);

	@Nullable
	public default SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS;
	}

	@Nullable
	public default ITextComponent getFailedMessage()
	{
		return new TextComponentTranslation("cavern.magic.condition.short");
	}
}