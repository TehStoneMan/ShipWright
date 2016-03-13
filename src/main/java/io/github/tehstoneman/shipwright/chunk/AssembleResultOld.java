package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.block.ShipBlocks;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.util.MaterialDensity;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class AssembleResultOld
{
	public static final int		RESULT_NONE		= 0, RESULT_OK = 1, RESULT_BLOCK_OVERFLOW = 2, RESULT_MISSING_MARKER = 3, RESULT_ERROR_OCCURED = 4,
			RESULT_BUSY_COMPILING = 5, RESULT_INCONSISTENT = 6, RESULT_OK_WITH_WARNINGS = 7;

	LocatedBlock				shipMarkingBlock;
	final List< LocatedBlock >	assembledBlocks	= new ArrayList< LocatedBlock >();
	int							resultCode;
	int							blockCount;
	int							balloonCount;
	int							tileEntityCount;
	float						mass;

	public BlockPos				offsetPos;

	public AssembleResultOld( ByteBuf buf )
	{
		resultCode = buf.readByte();
		if( resultCode == RESULT_NONE )
			return;
		blockCount = buf.readInt();
		balloonCount = buf.readInt();
		tileEntityCount = buf.readInt();
		mass = buf.readFloat();
	}

	public AssembleResultOld( NBTTagCompound compound, World world )
	{
		resultCode = compound.getByte( "res" );
		blockCount = compound.getInteger( "blockc" );
		balloonCount = compound.getInteger( "balloonc" );
		tileEntityCount = compound.getInteger( "tec" );
		mass = compound.getFloat( "mass" );
		offsetPos = new BlockPos( compound.getInteger( "xO" ), compound.getInteger( "yO" ), compound.getInteger( "zO" ) );
		if( compound.hasKey( "list" ) )
		{
			final NBTTagList list = compound.getTagList( "list", 10 );
			for( int i = 0; i < list.tagCount(); i++ )
			{
				final NBTTagCompound comp = list.getCompoundTagAt( i );
				assembledBlocks.add( new LocatedBlock( comp, world ) );
			}
		}
		if( compound.hasKey( "marker" ) )
		{
			final NBTTagCompound comp = compound.getCompoundTag( "marker" );
			shipMarkingBlock = new LocatedBlock( comp, world );
		}
	}

	AssembleResultOld()
	{
		clear();
	}

	void assembleBlock( LocatedBlock lb )
	{
		assembledBlocks.add( lb );
		blockCount = assembledBlocks.size();
		if( lb.block == ShipBlocks.balloon )
			balloonCount++;
		if( lb.tileEntity != null )
			tileEntityCount++;
		mass += MaterialDensity.getDensity( lb.block );
		offsetPos = new BlockPos( Math.min( offsetPos.getX(), lb.coords.getX() ), Math.min( offsetPos.getY(), lb.coords.getY() ), Math.min(
				offsetPos.getZ(), lb.coords.getZ() ) );
	}

	public void clear()
	{
		resultCode = RESULT_NONE;
		shipMarkingBlock = null;
		assembledBlocks.clear();
		blockCount = balloonCount = tileEntityCount = 0;
		offsetPos = new BlockPos( 0, 0, 0 );
	}

	public EntityShip getEntity( World world )
	{
		if( !isOK() )
			return null;

		final EntityShip entity = new EntityShip( world );
		// entity.setPilotSeat(shipMarkingBlock..blockMeta & 3, shipMarkingBlock.coords.getX() - xOffset, shipMarkingBlock.coords.getY() - yOffset,
		// shipMarkingBlock.coords.getZ() - zOffset);
		entity.getShipChunk().setCreationSpotBiomeGen( world.getBiomeGenForCoords( shipMarkingBlock.coords ) );

		final boolean flag = world.getGameRules().getBoolean( "doTileDrops" );
		world.getGameRules().setOrCreateGameRule( "doTileDrops", "false" );

		try
		{
			TileEntity tileentity;
			BlockPos tPos;
			for( final LocatedBlock lb : assembledBlocks )
			{
				//tPos = lb.coords.add( offsetPos.multiply( -1 ) );
				tileentity = lb.tileEntity;
				if( tileentity != null || lb.block.hasTileEntity( lb.blockMeta ) && ( tileentity = world.getTileEntity( lb.coords ) ) != null )
					tileentity.validate();
				//if( entity.getShipChunk().setBlockIDWithMetadata( tPos, lb.block, lb.blockMeta ) ) entity.getShipChunk().setTileEntity( tPos, tileentity );
				// world.setBlockState(new BlockPos(lb.coords.getX(), lb.coords.getY(), lb.coords.getZ()), Blocks.air, 1, 2); //0b10
			}
			for( final LocatedBlock block : assembledBlocks )
				world.setBlockToAir( block.coords );
		}
		catch( final Exception e )
		{
			resultCode = RESULT_ERROR_OCCURED;
			e.printStackTrace();
			return null;
		}
		finally
		{
			world.getGameRules().setOrCreateGameRule( "doTileDrops", String.valueOf( flag ) );
		}

		entity.getShipChunk().setChunkModified();
		entity.getShipChunk().onChunkLoad();
		entity.setLocationAndAngles( offsetPos.getX() + entity.getShipChunk().getCenterX(), 0, offsetPos.getZ() + entity.getShipChunk().getCenterZ(), 0F, 0F );

		return entity;
	}

	public int getCode()
	{
		return resultCode;
	}

	public boolean isOK()
	{
		return resultCode == RESULT_OK || resultCode == RESULT_OK_WITH_WARNINGS;
	}

	public LocatedBlock getShipMarker()
	{
		return shipMarkingBlock;
	}

	public int getBlockCount()
	{
		return blockCount;
	}

	public int getBalloonCount()
	{
		return balloonCount;
	}

	public int getTileEntityCount()
	{
		return tileEntityCount;
	}

	public float getMass()
	{
		return mass;
	}

	public void checkConsistent( World world )
	{
		boolean warn = false;
		for( final LocatedBlock lb : assembledBlocks )
		{
			final IBlockState blockState = world.getBlockState( lb.coords );
			final Block block = blockState.getBlock();
			if( block != lb.block )
			{
				resultCode = RESULT_INCONSISTENT;
				return;
			}
			if( blockState != lb.blockMeta )
				warn = true;
		}
		resultCode = warn ? RESULT_OK_WITH_WARNINGS : RESULT_OK;
	}

	public void writeNBTFully( NBTTagCompound compound )
	{
		writeNBTMetadata( compound );
		final NBTTagList list = new NBTTagList();
		for( final LocatedBlock lb : assembledBlocks )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			lb.writeToNBT( comp );
			list.appendTag( comp );
		}
		compound.setTag( "list", list );

		if( shipMarkingBlock != null )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			shipMarkingBlock.writeToNBT( comp );
			compound.setTag( "marker", comp );
		}
	}

	public void writeNBTMetadata( NBTTagCompound compound )
	{
		compound.setByte( "res", (byte)getCode() );
		compound.setInteger( "blockc", getBlockCount() );
		compound.setInteger( "balloonc", getBalloonCount() );
		compound.setInteger( "tec", getTileEntityCount() );
		compound.setFloat( "mass", getMass() );
		compound.setInteger( "xO", offsetPos.getX() );
		compound.setInteger( "yO", offsetPos.getY() );
		compound.setInteger( "zO", offsetPos.getZ() );
	}
}
