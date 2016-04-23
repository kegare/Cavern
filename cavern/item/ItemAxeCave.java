package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;

public class ItemAxeCave extends ItemAxe
{
	public ItemAxeCave(ToolMaterial material, String name)
	{
		super(ToolMaterial.IRON);
		this.toolMaterial = material;
		this.efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
		this.damageVsEntity = 8.0F;
		this.attackSpeed = -3.0F;
		this.setMaxDamage(material.getMaxUses());
		this.setUnlocalizedName(name);
		this.setCreativeTab(Cavern.tabCavern);
	}

	protected Item setDamageVsEntiry(float damage)
	{
		damageVsEntity = damage;

		return this;
	}

	protected Item setAttackSpeed(float speed)
	{
		attackSpeed = speed;

		return this;
	}
}