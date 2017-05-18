package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrazySpider extends RenderSpider<EntitySpider>
{
	private static final ResourceLocation crazySpiderTexture = new ResourceLocation("cavern", "textures/entity/crazy_spider.png");

	public RenderCrazySpider(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpider entity)
	{
		return crazySpiderTexture;
	}

	@Override
	public void setLightmap(EntitySpider entity, float partialTicks) {}
}