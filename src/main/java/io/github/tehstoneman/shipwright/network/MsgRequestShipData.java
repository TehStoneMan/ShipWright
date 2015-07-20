package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgRequestShipData extends ASMessageShip
{
	public MsgRequestShipData()
	{
		super();
	}
	
	public MsgRequestShipData(EntityShip entityship)
	{
		super(entityship);
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		if (ship != null)
		{
			if (ship.getShipChunk().chunkTileEntityMap.isEmpty())
			{
				return;
			}
			
			MsgTileEntities msg = new MsgTileEntities(ship);
			ShipWright.instance.pipeline.sendTo(msg, (EntityPlayerMP) player);
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
}
