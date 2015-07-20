package io.github.tehstoneman.shipwright.network;

import io.github.tehstoneman.shipwright.ModInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;

/**
 * Packet pipeline class. Directs all registered packet data to be handled by
 * the packets themselves.
 *
 * @author sirgingalot some code from: cpw
 */
@ChannelHandler.Sharable
public class ASMessagePipeline extends MessageToMessageCodec< FMLProxyPacket, ASMessage >
{
	private EnumMap< Side, FMLEmbeddedChannel >				channels;
	private final LinkedList< Class< ? extends ASMessage >>	packets				= new LinkedList< Class< ? extends ASMessage >>();
	private boolean											isPostInitialized	= false;

	/**
	 * Register your packet with the pipeline. Discriminators are automatically
	 * set.
	 *
	 * @param class0
	 *            the class to register
	 *
	 * @return whether registration was successful. Failure may occur if 256
	 *         packets have been registered or if the registry already contains
	 *         this packet
	 */
	public boolean registerPacket( Class< ? extends ASMessage > class0 )
	{
		if( packets.size() > 256 )
		{
			LogManager.getLogger( ModInfo.MODID ).error( "More than 256 packets registered" );
			return false;
		}

		if( packets.contains( class0 ) )
		{
			LogManager.getLogger( ModInfo.MODID ).warn( "Packet already registered" );
			return false;
		}

		if( isPostInitialized )
		{
			LogManager.getLogger( ModInfo.MODID ).error( "Already post-initialized" );
			return false;
		}

		packets.add( class0 );
		return true;
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, ASMessage msg, List< Object > out ) throws Exception
	{
		final ByteBuf buffer = Unpooled.buffer();
		final Class< ? extends ASMessage > clazz = msg.getClass();
		if( !packets.contains( msg.getClass() ) )
			throw new NullPointerException( "No Packet Registered for: " + msg.getClass().getCanonicalName() );

		final byte discriminator = (byte)packets.indexOf( clazz );
		buffer.writeByte( discriminator );
		try
		{
			msg.encodeInto( ctx, buffer );
		}
		catch( final IOException e )
		{
			e.printStackTrace();
			throw e;
		}
		final FMLProxyPacket proxyPacket = new FMLProxyPacket( new PacketBuffer( buffer ), ctx.channel().attr( NetworkRegistry.FML_CHANNEL ).get() );
		out.add( proxyPacket );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, FMLProxyPacket msg, List< Object > out ) throws Exception
	{
		final ByteBuf payload = msg.payload();
		final byte discriminator = payload.readByte();
		final Class< ? extends ASMessage > clazz = packets.get( discriminator );
		if( clazz == null )
			throw new NullPointerException( "No packet registered for discriminator: " + discriminator );

		EntityPlayer player = null;
		final Side side = FMLCommonHandler.instance().getEffectiveSide();
		switch( side )
		{
		case CLIENT:
			player = getClientPlayer();
			break;
		case SERVER:
			final INetHandler netHandler = ctx.channel().attr( NetworkRegistry.NET_HANDLER ).get();
			player = ( (NetHandlerPlayServer)netHandler ).playerEntity;
			break;
		default:
		}

		final ASMessage pkt = clazz.newInstance();
		try
		{
			pkt.decodeInto( ctx, payload.slice(), player );
		}
		catch( final IOException e )
		{
			e.printStackTrace();
			throw e;
		}

		switch( side )
		{
		case CLIENT:
			pkt.handleClientSide( player );
			break;
		case SERVER:
			pkt.handleServerSide( player );
			break;
		default:
		}

		out.add( pkt );
	}

	public void initalize()
	{
		channels = NetworkRegistry.INSTANCE.newChannel( ModInfo.MODID, this );
	}

	public void postInitialize()
	{
		if( isPostInitialized )
			return;

		isPostInitialized = true;
		Collections.sort( packets, new Comparator< Class< ? extends ASMessage >>()
				{

			@Override
			public int compare( Class< ? extends ASMessage > clazz1, Class< ? extends ASMessage > clazz2 )
			{
				int com = String.CASE_INSENSITIVE_ORDER.compare( clazz1.getCanonicalName(), clazz2.getCanonicalName() );
				if( com == 0 )
					com = clazz1.getCanonicalName().compareTo( clazz2.getCanonicalName() );

				return com;
			}
				} );
	}

	@SideOnly( Side.CLIENT )
	private EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	/**
	 * Send this message to everyone.
	 * <p/>
	 * Adapted from CPW's code in
	 * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param message
	 *            The message to send
	 */
	public void sendToAll( ASMessage message )
	{
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGET ).set( FMLOutboundHandler.OutboundTarget.ALL );
		channels.get( Side.SERVER ).writeAndFlush( message );
	}

	/**
	 * Send this message to the specified player.
	 * <p/>
	 * Adapted from CPW's code in
	 * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param message
	 *            The message to send
	 * @param player
	 *            The player to send it to
	 */
	public void sendTo( ASMessage message, EntityPlayerMP player )
	{
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGET ).set( FMLOutboundHandler.OutboundTarget.PLAYER );
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGETARGS ).set( player );
		channels.get( Side.SERVER ).writeAndFlush( message );
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * <p/>
	 * Adapted from CPW's code in
	 * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param message
	 *            The message to send
	 * @param point
	 *            The
	 *            {@link cpw.mods.fml.common.network.NetworkRegistry.TargetPoint}
	 *            around which to send
	 */
	public void sendToAllAround( ASMessage message, NetworkRegistry.TargetPoint point )
	{
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGET ).set( FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT );
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGETARGS ).set( point );
		channels.get( Side.SERVER ).writeAndFlush( message );
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * <p/>
	 * Adapted from CPW's code in
	 * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param message
	 *            The message to send
	 * @param dimensionId
	 *            The dimension id to target
	 */
	public void sendToDimension( ASMessage message, int dimensionId )
	{
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGET ).set( FMLOutboundHandler.OutboundTarget.DIMENSION );
		channels.get( Side.SERVER ).attr( FMLOutboundHandler.FML_MESSAGETARGETARGS ).set( dimensionId );
		channels.get( Side.SERVER ).writeAndFlush( message );
	}

	/**
	 * Send this message to the server.
	 * <p/>
	 * Adapted from CPW's code in
	 * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param message
	 *            The message to send
	 */
	public void sendToServer( ASMessage message )
	{
		channels.get( Side.CLIENT ).attr( FMLOutboundHandler.FML_MESSAGETARGET ).set( FMLOutboundHandler.OutboundTarget.TOSERVER );
		channels.get( Side.CLIENT ).writeAndFlush( message );
	}
}
