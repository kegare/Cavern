package cavern.client;

import java.util.Map;

import com.google.common.collect.Maps;

import cavern.client.config.CaveConfigEntries;
import cavern.client.config.CycleIntegerEntry;
import cavern.client.config.SelectItemsEntry;
import cavern.client.config.SelectMobsEntry;
import cavern.client.config.general.MiningPointsEntry;
import cavern.client.renderer.RenderCavenicCreeper;
import cavern.client.renderer.RenderCavenicSkeleton;
import cavern.client.renderer.RenderCavenicSpider;
import cavern.client.renderer.RenderCavenicZombie;
import cavern.core.CommonProxy;
import cavern.entity.EntityCavenicCreeper;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicZombie;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	public static final Map<Block, Block> renderBlockMap = Maps.newHashMap();

	@Override
	public void initConfigEntries()
	{
		CaveConfigEntries.cycleInteger = CycleIntegerEntry.class;

		CaveConfigEntries.selectItems = SelectItemsEntry.class;
		CaveConfigEntries.selectMobs = SelectMobsEntry.class;

		CaveConfigEntries.miningPoints = MiningPointsEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		renderBlockMap.put(Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);

		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, (IRenderFactory<EntityCavenicSkeleton>)RenderCavenicSkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicCreeper.class, (IRenderFactory<EntityCavenicCreeper>)RenderCavenicCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicZombie.class, (IRenderFactory<EntityCavenicZombie>)RenderCavenicZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSpider.class, (IRenderFactory<EntityCavenicSpider>)RenderCavenicSpider::new);
	}

	@Override
	public String translate(String key)
	{
		return I18n.format(key);
	}

	@Override
	public String translateFormat(String key, Object... format)
	{
		return I18n.format(key, format);
	}
}