package cavern.core;

import cavern.util.CaveUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent("cave_portal");
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent("rank.promote");

	public static final CaveSoundEvent SPELLING = new CaveSoundEvent("magic.spelling");
	public static final CaveSoundEvent SPELLING_END = new CaveSoundEvent("magic.spelling.end");
	public static final CaveSoundEvent MAGIC_SUCCESS = new CaveSoundEvent("magic.success");
	public static final CaveSoundEvent MAGIC_SUCCESS_MISC = new CaveSoundEvent("magic.success.misc");
	public static final CaveSoundEvent MAGIC_SUCCESS_SHORT = new CaveSoundEvent("magic.success.short");
	public static final CaveSoundEvent MAGIC_HOLY = new CaveSoundEvent("magic.holy");
	public static final CaveSoundEvent MAGIC_SUMMON = new CaveSoundEvent("magic.summon");
	public static final CaveSoundEvent MAGIC_INFINITY = new CaveSoundEvent("magic.infinity");

	public static final CaveSoundEvent MUSIC_CAVE = new CaveSoundEvent("cavemusic.cave");
	public static final CaveSoundEvent MUSIC_UNREST = new CaveSoundEvent("cavemusic.unrest");
	public static final CaveSoundEvent MUSIC_AQUA = new CaveSoundEvent("cavemusic.aqua");
	public static final CaveSoundEvent MUSIC_HOPE = new CaveSoundEvent("cavemusic.hope");
	public static final CaveSoundEvent MUSIC_CAVENIA1 = new CaveSoundEvent("cavemusic.cavenia1");
	public static final CaveSoundEvent MUSIC_CAVENIA2 = new CaveSoundEvent("cavemusic.cavenia2");

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);

		registry.register(SPELLING);
		registry.register(SPELLING_END);
		registry.register(MAGIC_SUCCESS);
		registry.register(MAGIC_SUCCESS_MISC);
		registry.register(MAGIC_SUCCESS_SHORT);
		registry.register(MAGIC_HOLY);
		registry.register(MAGIC_SUMMON);
		registry.register(MAGIC_INFINITY);

		registry.register(MUSIC_CAVE);
		registry.register(MUSIC_UNREST);
		registry.register(MUSIC_AQUA);
		registry.register(MUSIC_HOPE);
		registry.register(MUSIC_CAVENIA1);
		registry.register(MUSIC_CAVENIA2);
	}

	public static class CaveSoundEvent extends SoundEvent
	{
		public CaveSoundEvent(ResourceLocation key)
		{
			super(key);
			this.setRegistryName(key);
		}

		public CaveSoundEvent(String key)
		{
			this(CaveUtils.getKey(key));
		}
	}
}