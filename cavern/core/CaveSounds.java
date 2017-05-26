package cavern.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cave_portal"));
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "rank.promote"));

	public static final CaveSoundEvent SPELLING = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "magic.spelling"));
	public static final CaveSoundEvent SPELLING_END = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "magic.spelling.end"));
	public static final CaveSoundEvent MAGIC_SUCCESS = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "magic.success"));

	public static final CaveSoundEvent MUSIC_CAVE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.cave"));
	public static final CaveSoundEvent MUSIC_UNREST = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.unrest"));
	public static final CaveSoundEvent MUSIC_AQUA = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.aqua"));
	public static final CaveSoundEvent MUSIC_HOPE = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.hope"));
	public static final CaveSoundEvent MUSIC_CAVENIA1 = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.cavenia1"));
	public static final CaveSoundEvent MUSIC_CAVENIA2 = new CaveSoundEvent(new ResourceLocation(Cavern.MODID, "cavemusic.cavenia2"));

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);

		registry.register(SPELLING);
		registry.register(SPELLING_END);
		registry.register(MAGIC_SUCCESS);

		registry.register(MUSIC_CAVE);
		registry.register(MUSIC_UNREST);
		registry.register(MUSIC_AQUA);
		registry.register(MUSIC_HOPE);
		registry.register(MUSIC_CAVENIA1);
		registry.register(MUSIC_CAVENIA2);
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