package io.github.tehstoneman.shipwright.network;

import org.apache.logging.log4j.LogManager;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgClientRenameShip implements IMessage
{
	public String	newName;
	public BlockPos	pos;

	public MsgClientRenameShip()
	{
		newName = "";
		pos = new BlockPos( 0, 0, 0 );
	}

	public MsgClientRenameShip( String newName, BlockPos pos )
	{
		this.newName = newName;
		this.pos = pos;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		newName = ByteBufUtils.readUTF8String( buf );

		final int x = buf.readInt();
		final int y = buf.readInt();
		final int z = buf.readInt();
		pos = new BlockPos( x, y, z );
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		ByteBufUtils.writeUTF8String( buf, newName );
		buf.writeInt( pos.getX() );
		buf.writeInt( pos.getY() );
		buf.writeInt( pos.getZ() );
	}

	public static class Handler implements IMessageHandler< MsgClientRenameShip, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgClientRenameShip message, MessageContext ctx )
		{
			LogManager.getLogger( ModInfo.MODID ).info( "Rename Ship message recieved" );
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

		protected void processMessage( MsgClientRenameShip message, EntityPlayerMP player )
		{
			player.addChatMessage( new ChatComponentText( "Renaming Ship" ) );
			final World world = player.worldObj;
			final TileEntity tileEntity = world.getTileEntity( message.pos );

			if( tileEntity != null && tileEntity instanceof TileEntityHelm )
			{
				( (TileEntityHelm)tileEntity ).getShipInfo().shipName = message.newName;
				tileEntity.markDirty();
			}
		}
	}
}
