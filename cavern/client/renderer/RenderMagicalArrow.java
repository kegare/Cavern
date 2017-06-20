package cavern.client.renderer;

import cavern.entity.EntityMagicalArrow;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMagicalArrow extends RenderArrow<EntityMagicalArrow>
{
	private static final ResourceLocation RES_MAGICAL_ARROW = new ResourceLocation("cavern", "textures/entity/projectiles/magical_arrow.png");

	public RenderMagicalArrow(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicalArrow entity)
	{
		return RES_MAGICAL_ARROW;
	}
}