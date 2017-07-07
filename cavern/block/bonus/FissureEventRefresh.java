package cavern.block.bonus;

import java.util.Random;

import cavern.api.IFissureBreakEvent;
import cavern.api.IMagicianStats;
import cavern.stats.MagicianStats;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FissureEventRefresh implements IFissureBreakEvent
{
	@Override
	public boolean onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, EntityPlayer player, Random random)
	{
		if (player == null)
		{
			return false;
		}

		boolean result = false;

		if (player.isInsideOfMaterial(Material.WATER))
		{
			player.setAir(300);

			result = true;
		}

		IMagicianStats magician = MagicianStats.get(player);
		int prev = magician.getMP();

		magician.addMP(50);

		if (prev != magician.getMP())
		{
			result = true;
		}

		return result;
	}
}