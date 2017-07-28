package cavern.magic;

import cavern.core.Cavern;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicExplosion implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicExplosion(int level, long time)
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
		return 30 * getMagicLevel();
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		Vec3d hitVec = ForgeHooks.rayTraceEyeHitVec(player, Cavern.proxy.getBlockReachDistance(player));

		if (hitVec != null)
		{
			executeExplosion(world, player, new BlockPos(hitVec));

			return true;
		}

		EnumFacing front = player.getHorizontalFacing();
		BlockPos pos = player.getPosition().up();
		int i = 0;

		do
		{
			pos = pos.offset(front);

			++i;
		}
		while (i < 2 + getMagicLevel() * 2 && world.isAirBlock(pos));

		executeExplosion(world, player, pos);

		return true;
	}

	protected void executeExplosion(World world, EntityPlayer player, BlockPos pos)
	{
		boolean grief = world.getGameRules().getBoolean("mobGriefing");

		MagicalExplosion.createExplosion(world, player, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 3.0F + 2.5F * getMagicLevel(), grief);

		CaveUtils.grantAdvancement(player, "magic_explosion");
	}
}