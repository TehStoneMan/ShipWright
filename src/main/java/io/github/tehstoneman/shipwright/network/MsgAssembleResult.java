package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.chunk.AssembleResult;
import io.github.tehstoneman.shipwright.inventory.ContainerHelm;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MsgAssembleResult implements IMessage
{
	public AssembleResult	result;
	public boolean			prevFlag;

	public MsgAssembleResult()
	{
		result = null;
		prevFlag = false;
	}

	public MsgAssembleResult( AssembleResult result, boolean prevFlag )
	{
		this.result = result;
		this.prevFlag = prevFlag;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		prevFlag = buf.readBoolean();
		result = new AssembleResult( buf );
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		buf.writeBoolean( prevFlag );
		if( result == null )
			buf.writeByte( AssembleResult.RESULT_NONE );
		else
		{
			buf.writeByte( result.getCode() );
			buf.writeInt( result.getBlockCount() );
			buf.writeInt( result.getBalloonCount() );
			buf.writeInt( result.getTileEntityCount() );
			buf.writeFloat( result.getMass() );
		}
	}

	public static class Handler implements IMessageHandler< MsgAssembleResult, IMessage >
	{
		@Override
		public IMessage onMessage( final MsgAssembleResult message, MessageContext ctx )
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

		protected void processMessage( WorldClient worldClient, MsgAssembleResult message )
		{
			final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if( player.openContainer instanceof ContainerHelm )
				if( message.prevFlag )
					( (ContainerHelm)player.openContainer ).tileEntity.setPrevAssembleResult( message.result );
				else
					( (ContainerHelm)player.openContainer ).tileEntity.setAssembleResult( message.result );
		}
	}
}
