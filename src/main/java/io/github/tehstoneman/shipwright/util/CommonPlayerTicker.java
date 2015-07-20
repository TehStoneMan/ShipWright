package io.github.tehstoneman.shipwright.util;

import io.github.tehstoneman.shipwright.entity.EntityParachute;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CommonPlayerTicker
{
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e)
	{
		if (e.phase == Phase.END && e.player.ridingEntity instanceof EntityParachute && e.player.ridingEntity.ticksExisted < 40)
		{
			if (e.player.isSneaking())
			{
				e.player.setSneaking(false);
			}
		}
	}
}
