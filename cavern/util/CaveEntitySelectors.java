package cavern.util;

import com.google.common.base.Predicate;

import cavern.api.ISummonMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;

public class CaveEntitySelectors
{
	public static final Predicate<Entity> NOT_SUMMON = new Predicate<Entity>()
	{
		@Override
		public boolean apply(Entity entity)
		{
			return !(entity instanceof ISummonMob);
		}
	};

	public static final Predicate<? super Entity> CAN_SUMMON_MOB_TARGET = new Predicate<Entity>()
	{
		@Override
		public boolean apply(Entity entity)
		{
			return entity instanceof IMob && !(entity instanceof ISummonMob);
		}
	};
}