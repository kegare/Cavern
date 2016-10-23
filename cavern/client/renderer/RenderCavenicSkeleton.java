package cavern.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCavenicSkeleton extends RenderSkeleton
{
	private static final ResourceLocation cavenicSkeletonTexture = new ResourceLocation("cavern", "textures/entity/cavenic_skeleton.png");

	public RenderCavenicSkeleton(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected void preRenderCallback(EntitySkeleton entity, float ticks)
	{
		GlStateManager.scale(1.1F, 1.1F, 1.1F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkeleton entity)
	{
		return cavenicSkeletonTexture;
	}
}