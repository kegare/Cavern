package cavern.client;

import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import cavern.client.config.CaveConfigEntries;
import cavern.client.config.CycleIntegerEntry;
import cavern.client.config.SelectBlocksEntry;
import cavern.client.config.SelectItemsEntry;
import cavern.client.config.SelectMobsEntry;
import cavern.client.config.common.MiningPointsEntry;
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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	public static final KeyBinding KEY_MINING_ASSIST = new KeyBinding("key.cavern.miningAssist", KeyConflictContext.IN_GAME, Keyboard.KEY_V, "key.categories.cavern");

	public static final Map<Block, Block> RENDER_BLOCK_MAP = Maps.newHashMap();

	@Override
	public void initConfigEntries()
	{
		CaveConfigEntries.cycleInteger = CycleIntegerEntry.class;

		CaveConfigEntries.selectBlocks = SelectBlocksEntry.class;
		CaveConfigEntries.selectItems = SelectItemsEntry.class;
		CaveConfigEntries.selectMobs = SelectMobsEntry.class;

		CaveConfigEntries.miningPoints = MiningPointsEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		RENDER_BLOCK_MAP.put(Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);

		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, RenderCavenicSkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicCreeper.class, RenderCavenicCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicZombie.class, RenderCavenicZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSpider.class, RenderCavenicSpider::new);
	}

	@Override
	public void registerKeyBindings()
	{
		ClientRegistry.registerKeyBinding(KEY_MINING_ASSIST);
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

	@Override
	public boolean isSinglePlayer()
	{
		return FMLClientHandler.instance().getClient().isSingleplayer();
	}

	@Override
	public EntityPlayer getClientPlayer()
	{
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

	@Override
	public boolean hasAchievementUnlocked(EntityPlayer player, Achievement achievement)
	{
		if (player != null && player instanceof EntityPlayerSP)
		{
			return ((EntityPlayerSP)player).getStatFileWriter().hasAchievementUnlocked(achievement);
		}

		return false;
	}
}