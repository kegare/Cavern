package cavern.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cave_portal"));
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "rank.promote"));

	public static final CaveSoundEvent MUSIC_CAVE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.cave"));
	public static final CaveSoundEvent MUSIC_UNREST = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.unrest"));
	public static final CaveSoundEvent MUSIC_AQUA = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.aqua"));
	public static final CaveSoundEvent MUSIC_HOPE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.hope"));

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);

		registry.register(MUSIC_CAVE);
		registry.register(MUSIC_UNREST);
		registry.register(MUSIC_AQUA);
		registry.register(MUSIC_HOPE);
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