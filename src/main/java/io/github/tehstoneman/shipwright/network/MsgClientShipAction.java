package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgClientShipAction extends ASMessageShip
{
	public int	actionID;

	public MsgClientShipAction()
	{
		super();
		actionID = 0;
	}

	public MsgClientShipAction( EntityShip entityship, int id )
	{
		super( entityship );
		actionID = id;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		super.fromBytes( buf );
		actionID = buf.readByte();
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		super.toBytes( buf );
		buf.writeByte( actionID );
	}

	public static class Handler implements IMessageHandler< MsgClientShipAction, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgClientShipAction message, MessageContext ctx )
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

		protected void processMessage( MsgClientShipAction message, EntityPlayerMP player )
		{
			if( message.ship != null && message.ship.riddenByEntity == player )
				switch( message.actionID )
				{
				case 1:
					message.ship.alignToGrid();
					message.ship.updateRiderPosition( player, new BlockPos( message.ship.seatX, message.ship.seatY, message.ship.seatZ ), 1 );
					message.ship.disassemble( false );
					break;
				case 2:
					message.ship.alignToGrid();
					message.ship.updateRiderPosition( player, new BlockPos( message.ship.seatX, message.ship.seatY, message.ship.seatZ ), 1 );
					message.ship.disassemble( true );
					break;
				case 3:
					message.ship.alignToGrid();
					break;
				default:
					break;
				}
		}
	}
}
