package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgControlInput extends ASMessageShip
{
	public int	control;

	public MsgControlInput()
	{
		super();
		control = 0;
	}

	public MsgControlInput( EntityShip entityship, int controlid )
	{
		super( entityship );
		control = controlid;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		super.fromBytes( buf );
		control = buf.readByte();
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		super.toBytes( buf );
		buf.writeByte( control );
	}

	public static class Handler implements IMessageHandler< MsgControlInput, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgControlInput message, MessageContext ctx )
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

		protected void processMessage( MsgControlInput message, EntityPlayerMP player )
		{
			if( message.ship != null )
				message.ship.getController().updateControl( message.ship, player, message.control );
		}
	}
}
