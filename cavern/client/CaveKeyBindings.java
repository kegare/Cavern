package cavern.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveKeyBindings
{
	public static final KeyBinding KEY_MINING_ASSIST = new KeyBinding("key.cavern.miningAssist", KeyConflictContext.IN_GAME, Keyboard.KEY_V, "key.categories.cavern");
	public static final KeyBinding KEY_MAGIC_SPELLING = new KeyBinding("key.cavern.magicSpelling", KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories.cavern");

	public static void registerKeyBindings()
	{
		ClientRegistry.registerKeyBinding(KEY_MINING_ASSIST);
		ClientRegistry.registerKeyBinding(KEY_MAGIC_SPELLING);
	}
}