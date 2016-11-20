package cavern.util;

import com.google.common.base.Objects;

import net.minecraft.block.state.IBlockState;

public class BreakSpeedCache
{
	private IBlockState blockState;
	private float breakSpeed;
	private long cachedTime;

	public BreakSpeedCache(IBlockState state, float speed, long time)
	{
		this.blockState = state;
		this.breakSpeed = speed;
		this.cachedTime = time;
	}

	public IBlockState getBlockState()
	{
		return blockState;
	}

	public float getBreakSpeed()
	{
		return breakSpeed;
	}

	public long getCachedTime()
	{
		return cachedTime;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof BreakSpeedCache))
		{
			return false;
		}

		BreakSpeedCache entry = (BreakSpeedCache)obj;

		if (!CaveUtils.areBlockStatesEqual(blockState, entry.blockState))
		{
			return false;
		}

		return breakSpeed == entry.breakSpeed;
	}

	@Override
	public int hashCode()
	{
		int meta = blockState.getBlock().getMetaFromState(blockState);

		return Objects.hashCode(blockState.getBlock(), meta, breakSpeed);
	}
}