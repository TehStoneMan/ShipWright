package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgFarInteract extends ASMessageShip
{
	public MsgFarInteract()
	{
		super();
	}

	public MsgFarInteract( EntityShip entityship )
	{
		super( entityship );
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		super.fromBytes( buf );
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		super.toBytes( buf );
	}

	public static class Handler implements IMessageHandler< MsgFarInteract, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgFarInteract message, MessageContext ctx )
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

		protected void processMessage( MsgFarInteract message, EntityPlayerMP player )
		{
			if( message.ship != null )
				player.interactWith( message.ship );
		}
	}
}
