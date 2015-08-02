package io.github.tehstoneman.shipwright.util;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.network.MsgRequestShipData;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class ClientHookContainer extends CommonHookContainer
{
	@SubscribeEvent
	public void onEntitySpawn( EntityJoinWorldEvent event )
	{
		if( event.world.isRemote && event.entity instanceof EntityShip )
		{
			if( ( (EntityShip)event.entity ).getShipChunk().chunkTileEntityMap.isEmpty() )
				return;

			// MsgRequestShipData msg = new MsgRequestShipData((EntityShip) event.entity);
			// ShipWright.instance.pipeline.sendToServer(msg);
			ShipWright.network.sendToServer( new MsgRequestShipData( (EntityShip)event.entity ) );
		}
	}
}
