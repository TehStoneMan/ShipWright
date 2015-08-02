package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.chunk.MobileChunkClient;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgTileEntities extends ASMessageShip
{
	private NBTTagCompound	tagCompound;

	public MsgTileEntities()
	{
		super();
		tagCompound = null;
	}

	public MsgTileEntities( EntityShip entityship )
	{
		super( entityship );
		tagCompound = null;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		super.fromBytes( buf );
		if( ship != null )
		{
			final DataInputStream in = new DataInputStream( new ByteBufInputStream( buf ) );
			try
			{
				tagCompound = CompressedStreamTools.read( in );
			}
			catch( final IOException e )
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					in.close();
				}
				catch( final IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		super.toBytes( buf );
		tagCompound = new NBTTagCompound();
		final NBTTagList list = new NBTTagList();
		for( final TileEntity te : ship.getShipChunk().chunkTileEntityMap.values() )
		{
			final NBTTagCompound nbt = new NBTTagCompound();
			if( te instanceof TileEntityHelm )
				( (TileEntityHelm)te ).writeNBTforSending( nbt );
			else
				te.writeToNBT( nbt );
			list.appendTag( nbt );
		}
		tagCompound.setTag( "list", list );
		final DataOutputStream out = new DataOutputStream( new ByteBufOutputStream( buf ) );
		try
		{
			CompressedStreamTools.write( tagCompound, out );
			out.flush();
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				out.close();
			}
			catch( final IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	public static class Handler implements IMessageHandler< MsgTileEntities, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgTileEntities message, MessageContext ctx )
		{
			if( ctx.side == Side.CLIENT )
			{
				final Minecraft minecraft = Minecraft.getMinecraft();
				final WorldClient worldClient = minecraft.theWorld;
				minecraft.addScheduledTask( new Runnable()
				{
					@Override
					public void run()
					{
						processMessage( worldClient, message );
					}
				} );
			}
			return null;
		}

		protected void processMessage( WorldClient worldClient, MsgTileEntities message )
		{
			if( message.ship != null && message.tagCompound != null )
			{
				final NBTTagList list = message.tagCompound.getTagList( "list", 10 );
				for( int i = 0; i < list.tagCount(); i++ )
				{
					final NBTTagCompound nbt = list.getCompoundTagAt( i );
					if( nbt == null )
						continue;
					final int x = nbt.getInteger( "x" );
					final int y = nbt.getInteger( "y" );
					final int z = nbt.getInteger( "z" );
					try
					{
						final TileEntity te = message.ship.getShipChunk().getTileEntity( new BlockPos( x, y, z ) );
						te.readFromNBT( nbt );
					}
					catch( final Exception e )
					{
						e.printStackTrace();
					}
				}
				( (MobileChunkClient)message.ship.getShipChunk() ).getRenderer().markDirty();
			}
		}
	}
}
