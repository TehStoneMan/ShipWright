package io.github.tehstoneman.shipwright.entity;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.MobileChunkServer;
import io.github.tehstoneman.shipwright.network.MsgChunkBlockUpdate;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ShipHandlerServer extends ShipHandlerCommon
{
	private boolean	firstChunkUpdate;
	
	public ShipHandlerServer(EntityShip entityship)
	{
		super(entityship);
		firstChunkUpdate = true;
	}
	
	@Override
	public boolean interact(EntityPlayer player)
	{
		if (ship.riddenByEntity == null)
		{
			player.mountEntity(ship);
			return true;
		} else if (player.ridingEntity == null)
		{
			return ship.getCapabilities().mountEntity(player);
		}
		
		return false;
	}
	
	@Override
	public void onChunkUpdate()
	{
		super.onChunkUpdate();
		Collection<BlockPos> list = ((MobileChunkServer) ship.getShipChunk()).getSendQueue();
		if (firstChunkUpdate)
		{
			ship.getCapabilities().spawnSeatEntities();
		} else
		{
			MsgChunkBlockUpdate msg = new MsgChunkBlockUpdate(ship, list);
			ShipWright.instance.pipeline.sendToAllAround(msg, new TargetPoint(ship.worldObj.provider.getDimensionId(), ship.posX, ship.posY, ship.posZ, 64D));
		}
		list.clear();
		firstChunkUpdate = false;
	}
}
