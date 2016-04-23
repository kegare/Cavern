package cavern.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent cave_portal = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cave_portal"));
	public static final CaveSoundEvent rank_promote = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "rank.promote"));

	public static final CaveSoundEvent music_cave = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.cave"));
	public static final CaveSoundEvent music_unrest = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.unrest"));
	public static final CaveSoundEvent music_aqua = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.aqua"));
	public static final CaveSoundEvent music_hope = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.hope"));

	public static void registerSounds()
	{
		GameRegistry.register(cave_portal);
		GameRegistry.register(rank_promote);

		GameRegistry.register(music_cave);
		GameRegistry.register(music_unrest);
		GameRegistry.register(music_aqua);
		GameRegistry.register(music_hope);
	}

	public static class CaveSoundEvent extends SoundEvent
	{
		public CaveSoundEvent(ResourceLocation soundName)
		{
			super(soundName);
			this.setRegistryName(soundName);
		}
	}
}