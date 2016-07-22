package cavern.client.gui;

import java.util.Collection;

import cavern.util.BlockMeta;

public interface IBlockSelector
{
	public void onBlockSelected(int id, Collection<BlockMeta> selected);

	public default boolean canSelectBlock(int id, BlockMeta blockMeta)
	{
		return true;
	}
}