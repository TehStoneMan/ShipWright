package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgClientHelmAction implements IMessage
{
	public int				actionID;
	public BlockPos			pos;

	public static final int	BUILD	= 0;
	public static final int	MOUNT	= 1;
	public static final int	UNDO	= 2;

	public MsgClientHelmAction()
	{
		actionID = -1;
		pos = new BlockPos( 0, 0, 0 );
	}

	public MsgClientHelmAction( int actionID, BlockPos pos )
	{
		this.actionID = actionID;
		this.pos = pos;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		actionID = buf.readByte();
		final int x = buf.readInt();
		final int y = buf.readInt();
		final int z = buf.readInt();
		pos = new BlockPos( x, y, z );
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		buf.writeByte( actionID );
		buf.writeInt( pos.getX() );
		buf.writeInt( pos.getY() );
		buf.writeInt( pos.getZ() );
	}

	public static class Handler implements IMessageHandler< MsgClientHelmAction, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgClientHelmAction message, MessageContext ctx )
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

		protected void processMessage( MsgClientHelmAction message, EntityPlayerMP player )
		{
			final World world = player.worldObj;
			final TileEntity tileEntity = world.getTileEntity( message.pos );

			if( tileEntity != null && tileEntity instanceof TileEntityHelm )
				switch( message.actionID )
				{
				case BUILD:
					( (TileEntityHelm)tileEntity ).assembleShip( player );
					break;
				case MOUNT:
					( (TileEntityHelm)tileEntity ).mountShip( player );
					break;
				case UNDO:
					( (TileEntityHelm)tileEntity ).undoCompilation( player );
					break;
				default:
					break;
				}
		}
	}
}
