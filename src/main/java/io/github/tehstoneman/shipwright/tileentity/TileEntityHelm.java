package io.github.tehstoneman.shipwright.tileentity;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.AssembleResultOld;
import io.github.tehstoneman.shipwright.chunk.ChunkAssembler;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.entity.IShipTileEntity;
import io.github.tehstoneman.shipwright.entity.ShipInfo;
import io.github.tehstoneman.shipwright.network.MsgAssembleResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TileEntityHelm extends TileEntity implements IShipTileEntity
{
	private EntityShip	activeShip;
	private ShipInfo	info;
	private AssembleResultOld	assembleResult, prevResult;

	public TileEntityHelm()
	{
		super();
		info = new ShipInfo();
		activeShip = null;
		assembleResult = prevResult = null;
	}

	public ShipInfo getShipInfo()
	{
		return info;
	}

	public AssembleResultOld getAssembleResult()
	{
		return assembleResult;
	}

	public AssembleResultOld getPrevAssembleResult()
	{
		return prevResult;
	}

	public void setShipInfo( ShipInfo shipinfo )
	{
		if( shipinfo == null )
			throw new NullPointerException( "Cannot set null ship info" );
		info = shipinfo;
	}

	public void setAssembleResult( AssembleResultOld result )
	{
		assembleResult = result;
	}

	public void setPrevAssembleResult( AssembleResultOld result )
	{
		prevResult = result;
	}

	@Override
	public void setParentShip( EntityShip entityship, int x, int y, int z )
	{
		activeShip = entityship;
	}

	@Override
	public EntityShip getParentShip()
	{
		return activeShip;
	}

	public boolean assembleShip( EntityPlayer player )
	{
		if( !worldObj.isRemote )
		{
			prevResult = assembleResult;
			final ChunkAssembler assembler = new ChunkAssembler( worldObj, pos );
			assembleResult = assembler.doAssemble();

			sendAssembleResult( player, false );
			sendAssembleResult( player, true );

			ChatComponentText c;
			switch( assembleResult.getCode() )
			{
			case AssembleResultOld.RESULT_OK:
			case AssembleResultOld.RESULT_OK_WITH_WARNINGS:
				return true;
			case AssembleResultOld.RESULT_BLOCK_OVERFLOW:
				c = new ChatComponentText( "Cannot create ship with more than " + ShipWright.instance.modConfig.maxShipChunkBlocks + " blocks" );
				player.addChatMessage( c );
				break;
			case AssembleResultOld.RESULT_MISSING_MARKER:
				c = new ChatComponentText( "Cannot create ship with no ship marker" );
				player.addChatMessage( c );
				break;
			case AssembleResultOld.RESULT_ERROR_OCCURED:
				c = new ChatComponentText( "An error occured while assembling ship. See console log for details." );
				player.addChatMessage( c );
				break;
			case AssembleResultOld.RESULT_NONE:
				c = new ChatComponentText( "Nothing was assembled" );
				player.addChatMessage( c );
				break;
			default:
			}
		}
		return false;
	}

	public boolean mountShip( EntityPlayer player )
	{
		if( !worldObj.isRemote )
			if( assembleResult != null && assembleResult.isOK() )
			{
				assembleResult.checkConsistent( worldObj );
				sendAssembleResult( player, false );
				if( assembleResult.getCode() == AssembleResultOld.RESULT_INCONSISTENT )
					return false;
				if( assembleResult.getCode() == AssembleResultOld.RESULT_OK_WITH_WARNINGS )
				{
					final IChatComponent c = new ChatComponentText( "Ship contains changes" );
					player.addChatMessage( c );
				}

				final EntityShip entity = assembleResult.getEntity( worldObj );
				if( entity != null )
				{
					entity.setInfo( info );
					if( worldObj.spawnEntityInWorld( entity ) )
					{
						player.mountEntity( entity );
						assembleResult = null;
						// entity.getCapabilities().mountEntity(entityplayer);
						return true;
					}
				}
			}
		return false;
	}

	public void undoCompilation( EntityPlayer player )
	{
		assembleResult = prevResult;
		prevResult = null;

		sendAssembleResult( player, false );
		sendAssembleResult( player, true );
	}

	public void sendAssembleResult( EntityPlayer player, boolean prev )
	{
		if( !worldObj.isRemote )
		{
			AssembleResultOld res;
			if( prev )
				res = prevResult;
			else
				res = assembleResult;
			ShipWright.network.sendTo( new MsgAssembleResult( res, prev ), (EntityPlayerMP)player );
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		final NBTTagCompound compound = new NBTTagCompound();
		writeNBTforSending( compound );
		return new S35PacketUpdateTileEntity( pos, 0, compound );
	}

	@Override
	public void onDataPacket( NetworkManager net, S35PacketUpdateTileEntity packet )
	{
		readFromNBT( packet.getNbtCompound() );
	}

	@Override
	public void readFromNBT( NBTTagCompound compound )
	{
		super.readFromNBT( compound );
		// blockMetadata = compound.getInteger("meta");
		info.shipName = compound.getString( "name" );
		if( compound.hasKey( "ship" ) && worldObj != null )
		{
			final int id = compound.getInteger( "ship" );
			final Entity entity = worldObj.getEntityByID( id );
			if( entity instanceof EntityShip )
				activeShip = (EntityShip)entity;
		}
		if( compound.hasKey( "res" ) )
			assembleResult = new AssembleResultOld( compound.getCompoundTag( "res" ), worldObj );
	}

	@Override
	public void writeToNBT( NBTTagCompound compound )
	{
		super.writeToNBT( compound );
		// compound.setInteger("meta", blockMetadata);
		compound.setString( "name", info.shipName );
		if( activeShip != null && !activeShip.isDead )
			compound.setInteger( "ship", activeShip.getEntityId() );
		if( assembleResult != null )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			assembleResult.writeNBTFully( comp );
			compound.setTag( "res", comp );
		}
	}

	public void writeNBTforSending( NBTTagCompound compound )
	{
		super.writeToNBT( compound );
		// compound.setInteger("meta", blockMetadata);
		compound.setString( "name", info.shipName );
		if( activeShip != null && !activeShip.isDead )
			compound.setInteger( "ship", activeShip.getEntityId() );
		if( assembleResult != null )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			assembleResult.writeNBTMetadata( comp );
			compound.setTag( "res", comp );
		}
	}
}
