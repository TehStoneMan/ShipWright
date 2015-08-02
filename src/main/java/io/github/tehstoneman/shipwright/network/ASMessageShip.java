package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;

public abstract class ASMessageShip implements IMessage
{
	public EntityShip	ship;
	public int			entityID;

	public ASMessageShip()
	{
		entityID = 0;
		ship = null;
	}

	public ASMessageShip( EntityShip entityship )
	{
		entityID = entityship.getEntityId();
		ship = entityship;
	}

	@Override
	public void fromBytes( ByteBuf buf )
	{
		final int entityid = buf.readInt();
	}

	@Override
	public void toBytes( ByteBuf buf )
	{
		buf.writeInt( ship.getEntityId() );
	}

	public static class Handler implements IMessageHandler< ASMessageShip, IMessage >
	{
		@Override
		public IMessage onMessage( final ASMessageShip message, MessageContext ctx )
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

		protected void processMessage( ASMessageShip message, EntityPlayerMP player )
		{
			final Entity entity = player.worldObj.getEntityByID( message.entityID );
			if( entity instanceof EntityShip )
				message.ship = (EntityShip)entity;
			else
				LogManager.getLogger( ModInfo.MODID ).warn( "Unable to find ship entity for ID " + message.entityID );
		}
	}
}
