package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class MsgClientRenameShip extends ASMessage
{
	public TileEntityHelm	tileEntity;
	public String			newName;
	
	public MsgClientRenameShip()
	{
		tileEntity = null;
		newName = "";
	}
	
	public MsgClientRenameShip(TileEntityHelm te, String name)
	{
		tileEntity = te;
		newName = name;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf)
	{
		buf.writeShort(newName.length());
		buf.writeBytes(newName.getBytes());
		buf.writeInt(tileEntity.getPos().getX());
		buf.writeInt(tileEntity.getPos().getY());
		buf.writeInt(tileEntity.getPos().getZ());
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player)
	{
		byte[] ab = new byte[buf.readShort()];
		buf.readBytes(ab);
		newName = new String(ab);
		
		TileEntity te = player.worldObj.getTileEntity(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
		if (te instanceof TileEntityHelm)
		{
			tileEntity = (TileEntityHelm) te;
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		if (tileEntity != null)
		{
			tileEntity.getShipInfo().shipName = newName;
		}
	}
	
}
