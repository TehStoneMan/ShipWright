package io.github.tehstoneman.shipwright.control;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.network.MsgControlInput;
import net.minecraft.entity.player.EntityPlayer;

public class ShipControllerClient extends ShipControllerCommon
{
	@Override
	public void updateControl(EntityShip ship, EntityPlayer player, int i)
	{
		super.updateControl(ship, player, i);
		MsgControlInput msg = new MsgControlInput(ship, i);
		ShipWright.instance.pipeline.sendToServer(msg);
	}
}
