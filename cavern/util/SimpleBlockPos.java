package cavern.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SimpleBlockPos extends BlockPos
{
	private int x;
	private int y;
	private int z;
	private int originX;
	private int originY;
	private int originZ;

	public SimpleBlockPos()
	{
		this(0, 0, 0);
	}

	public SimpleBlockPos(BlockPos pos)
	{
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public SimpleBlockPos(int x_, int y_, int z_)
	{
		super(0, 0, 0);
		this.x = x_;
		this.y = y_;
		this.z = z_;
		this.originX = x_;
		this.originY = y_;
		this.originZ = z_;
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getZ()
	{
		return z;
	}

	public int getOriginX()
	{
		return originX;
	}

	public int getOriginY()
	{
		return originY;
	}

	public int getOriginZ()
	{
		return originZ;
	}

	public SimpleBlockPos set(int xIn, int yIn, int zIn)
	{
		x = xIn;
		y = yIn;
		z = zIn;

		return this;
	}

	public SimpleBlockPos set(BlockPos pos)
	{
		return set(pos.getX(), pos.getY(), pos.getZ());
	}

	public SimpleBlockPos setOrigin(int xIn, int yIn, int zIn)
	{
		originX = xIn;
		originY = yIn;
		originZ = zIn;

		return this;
	}

	public SimpleBlockPos setOrigin(BlockPos pos)
	{
		return setOrigin(pos.getX(), pos.getY(), pos.getZ());
	}

	public SimpleBlockPos setOrigin()
	{
		return setOrigin(getX(), getY(), getZ());
	}

	public SimpleBlockPos origin()
	{
		return set(getOriginX(), getOriginY(), getOriginZ());
	}

	public void setY(int yIn)
	{
		y = yIn;
	}

	@Override
	public SimpleBlockPos add(int x, int y, int z)
	{
		return x == 0 && y == 0 && z == 0 ? this : set(getX() + x, getY() + y, getZ() + z);
	}

	@Override
	public SimpleBlockPos up()
	{
		return up(1);
	}

	@Override
	public SimpleBlockPos up(int n)
	{
		return offset(EnumFacing.UP, n);
	}

	@Override
	public SimpleBlockPos down()
	{
		return down(1);
	}

	@Override
	public SimpleBlockPos down(int n)
	{
		return offset(EnumFacing.DOWN, n);
	}

	@Override
	public SimpleBlockPos north()
	{
		return north(1);
	}

	@Override
	public SimpleBlockPos north(int n)
	{
		return offset(EnumFacing.NORTH, n);
	}

	@Override
	public SimpleBlockPos south()
	{
		return south(1);
	}

	@Override
	public SimpleBlockPos south(int n)
	{
		return offset(EnumFacing.SOUTH, n);
	}

	@Override
	public SimpleBlockPos west()
	{
		return west(1);
	}

	@Override
	public SimpleBlockPos west(int n)
	{
		return offset(EnumFacing.WEST, n);
	}

	@Override
	public SimpleBlockPos east()
	{
		return east(1);
	}

	@Override
	public SimpleBlockPos east(int n)
	{
		return offset(EnumFacing.EAST, n);
	}

	@Override
	public SimpleBlockPos offset(EnumFacing facing)
	{
		return offset(facing, 1);
	}

	@Override
	public SimpleBlockPos offset(EnumFacing facing, int n)
	{
		return n == 0 ? this : set(getX() + facing.getFrontOffsetX() * n, getY() + facing.getFrontOffsetY() * n, getZ() + facing.getFrontOffsetZ() * n);
	}

	@Override
	public BlockPos toImmutable()
	{
		return new BlockPos(this);
	}
}