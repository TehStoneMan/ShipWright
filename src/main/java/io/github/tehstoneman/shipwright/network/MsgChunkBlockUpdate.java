package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.chunk.ChunkIO;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException
	{
		super.encodeInto(ctx, buf);
		ChunkIO.writeCompressed(buf, ship.getShipChunk(), sendQueue);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException
	{
		super.decodeInto(ctx, buf, player);
		if (ship != null)
		{
			ChunkIO.readCompressed(buf, ship.getShipChunk());
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
	}
}
