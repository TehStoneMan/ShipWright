package io.github.tehstoneman.shipwright.entity;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.network.MsgFarInteract;
import net.minecraft.entity.player.EntityPlayer;

public class ShipHandlerClient extends ShipHandlerCommon
{
	public ShipHandlerClient(EntityShip entityship)
	{
		super(entityship);
	}
	
	@Override
	public boolean interact(EntityPlayer player)
	{
		if (player.getDistanceSqToEntity(ship) >= 36D)
		{
			MsgFarInteract msg = new MsgFarInteract(ship);
			ShipWright.instance.pipeline.sendToServer(msg);
		}
		
		return super.interact(player);
	}
	
	@Override
	public void onChunkUpdate()
	{
		super.onChunkUpdate();
	}
}
