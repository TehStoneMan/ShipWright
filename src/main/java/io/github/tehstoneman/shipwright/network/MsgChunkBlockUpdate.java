package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.chunk.ChunkIO;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgChunkBlockUpdate extends ASMessageShip
{
	private Collection<BlockPos>	sendQueue;
	
	public MsgChunkBlockUpdate()
	{
	}
	
	public MsgChunkBlockUpdate(EntityShip entityship, Collection<BlockPos> blocks)
	{
		super(entityship);
		sendQueue = blocks;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes( buf);
		if (ship != null)
		{
			try
			{
				ChunkIO.readCompressed(buf, ship.getShipChunk());
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes( buf);
		try
		{
			ChunkIO.writeCompressed(buf, ship.getShipChunk(), sendQueue);
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
	}
	*/
	public static class Handler implements IMessageHandler< MsgChunkBlockUpdate, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgChunkBlockUpdate message, MessageContext ctx )
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

		protected void processMessage( MsgChunkBlockUpdate message, EntityPlayerMP player )
		{
			final Entity entity = player.worldObj.getEntityByID( message.entityID );
			if( entity instanceof EntityShip )
				message.ship = (EntityShip)entity;
			else
				LogManager.getLogger( ModInfo.MODID ).warn( "Unable to find ship entity for ID " + message.entityID );
		}
	}
}
