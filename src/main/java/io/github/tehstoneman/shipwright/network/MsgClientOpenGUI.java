package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ShipWright;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgClientOpenGUI implements IMessage
{
	public int	guiID;

	public MsgClientOpenGUI()
	{
		guiID = 0;
	}

	public MsgClientOpenGUI( int id )
	{
		guiID = id;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		guiID = buf.readInt();
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		buf.writeInt( guiID );
	}

	public static class Handler implements IMessageHandler< MsgClientOpenGUI, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgClientOpenGUI message, MessageContext ctx )
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

		protected void processMessage( MsgClientOpenGUI message, EntityPlayerMP player )
		{
			player.openGui( ShipWright.instance, message.guiID, player.worldObj, 0, 0, 0 );
		}
	}
}
