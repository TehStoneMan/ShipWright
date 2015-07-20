package io.github.tehstoneman.shipwright.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ASMessage
{
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException;
	
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException;
	
	@SideOnly(Side.CLIENT)
	public abstract void handleClientSide(EntityPlayer player);
	
	public abstract void handleServerSide(EntityPlayer player);
}
