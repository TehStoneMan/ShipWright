package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgRequestShipData extends ASMessageShip
{
	public MsgRequestShipData()
	{
		super();
	}

	public MsgRequestShipData( EntityShip entityship )
	{
		super( entityship );
	}

	public static class Handler implements IMessageHandler< MsgRequestShipData, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgRequestShipData message, MessageContext ctx )
		{
			if( ctx.side == Side.SERVER )
			{
				final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				if( player != null )
				{
					final WorldServer playerWorldServer = player.getServerForPlayer();
					playerWorldServer.addScheduledTask( new Runnable()
					{
						@Override
						public void run()
						{
							processMessage( message, player );
						}
					} );
				}
			}
			return null;
		}

		protected void processMessage( MsgRequestShipData message, EntityPlayerMP player )
		{
			if( message.ship != null )
			{
				if( message.ship.getShipChunk().chunkTileEntityMap.isEmpty() )
					return;

				// MsgTileEntities msg = new MsgTileEntities(message.ship);
				// ShipWright.instance.pipeline.sendTo(msg, (EntityPlayerMP) player);
				ShipWright.network.sendTo( new MsgTileEntities( message.ship ), player );
			}
		}
	}
}
