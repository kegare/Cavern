package cavern.config.manager;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import cavern.util.BlockMeta;
import net.minecraftforge.common.config.Configuration;

public class CaveVeinManager
{
	private final List<CaveVein> CAVE_VEINS = Lists.newArrayList();

	public Configuration config;

	private int type;

	public CaveVeinManager(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public boolean addCaveVein(CaveVein vein)
	{
		return getCaveVeins().add(vein);
	}

	public boolean removeCaveVein(CaveVein vein)
	{
		return getCaveVeins().remove(vein);
	}

	public boolean removeCaveVeins(BlockMeta target)
	{
		Iterator<CaveVein> iterator = getCaveVeins().iterator();
		boolean removed = false;

		while (iterator.hasNext())
		{
			CaveVein vein = iterator.next();

			if (vein.getBlockMeta().equals(target))
			{
				removed = true;

				iterator.remove();
			}
		}

		return removed;
	}

	public List<CaveVein> getCaveVeins()
	{
		return CAVE_VEINS;
	}
}