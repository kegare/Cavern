package cavern.magic;

import cavern.core.CaveSounds;
import cavern.entity.EntityMagicTorcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicTorch implements IMagic
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

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		return player.isSneaking() ? magicSpellTime : magicSpellTime / 2;
	}

	public double getMagicRange(EntityPlayer player)
	{
		return player.isSneaking() ? magicRange : 6.0D;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 10 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
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

		return IMagic.super.getFailedMessage();
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		if (!player.inventory.hasItemStack(new ItemStack(Blocks.TORCH)))
		{
			errorCode = 1;

			return false;
		}

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