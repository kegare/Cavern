package cavern.client;

import java.util.Map;

import com.google.common.collect.Maps;

import cavern.client.renderer.RenderCavenicCreeper;
import cavern.client.renderer.RenderCavenicSkeleton;
import cavern.client.renderer.RenderCavenicSpider;
import cavern.client.renderer.RenderCavenicZombie;
import cavern.entity.EntityCavenicCreeper;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicZombie;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveRenderingRegistry
{
	public static final Map<Block, Block> RENDER_BLOCK_MAP = Maps.newHashMap();

	public static void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, RenderCavenicSkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicCreeper.class, RenderCavenicCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicZombie.class, RenderCavenicZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSpider.class, RenderCavenicSpider::new);
	}

	public static void registerRenderBlocks()
	{
		RENDER_BLOCK_MAP.put(Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
	}

	public static Block getRenderBlock(Block block)
	{
		Block ret = RENDER_BLOCK_MAP.get(block);

		return ret == null ? block : ret;
	}
}