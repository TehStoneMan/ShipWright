package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.ShipBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;

public class ChunkAssembler
{
	private final World		worldObj;
	public final BlockPos	startPos;

	private final int		maxBlocks;

	public ChunkAssembler( World world, BlockPos pos )
	{
		worldObj = world;
		startPos = pos;
		maxBlocks = ShipWright.instance.modConfig.maxShipChunkBlocks;
	}

	public AssembleResult doAssemble()
	{
		final AssembleResult result = new AssembleResult();
		result.offsetPos = startPos;
		try
		{
			if( ShipWright.instance.modConfig.useNewAlgorithm )
				assembleIterative( result, startPos );
			else
				assembleRecursive( result, new HashSet< BlockPos >(), startPos );
			if( result.shipMarkingBlock == null )
				result.resultCode = AssembleResult.RESULT_MISSING_MARKER;
			else
				result.resultCode = AssembleResult.RESULT_OK;
		}
		catch( final ShipSizeOverflowException e )
		{
			result.resultCode = AssembleResult.RESULT_BLOCK_OVERFLOW;
		}
		catch( final Error e )
		{
			LogManager.getLogger( ModInfo.MODID ).error( "Error while compiling ship", e );
			result.resultCode = AssembleResult.RESULT_ERROR_OCCURED;
		}
		return result;
	}

	private void assembleIterative( AssembleResult result, BlockPos sPos ) throws ShipSizeOverflowException
	{
		final HashSet< BlockPos > openset = new HashSet< BlockPos >();
		final HashSet< BlockPos > closedset = new HashSet< BlockPos >();
		final List< BlockPos > iterator = new ArrayList< BlockPos >();

		BlockPos tPos = sPos;

		openset.add( sPos );
		while( !openset.isEmpty() )
		{
			iterator.addAll( openset );
			for( final BlockPos pos : iterator )
			{
				openset.remove( pos );

				if( closedset.contains( pos ) )
					continue;
				if( result.assembledBlocks.size() > maxBlocks )
					throw new ShipSizeOverflowException();

				tPos = pos;

				closedset.add( pos );

				final IBlockState blockState = worldObj.getBlockState( pos );
				final Block block = blockState.getBlock();
				if( !canUseBlockForVehicle( block, tPos ) )
					continue;

				final LocatedBlock lb = new LocatedBlock( block, worldObj.getBlockState( tPos ), worldObj.getTileEntity( tPos ), pos );
				result.assembleBlock( lb );
				if( block == ShipBlocks.helm && result.shipMarkingBlock == null )
					result.shipMarkingBlock = lb;

				openset.add( tPos.add( -1,  0,  0 ) );
				openset.add( tPos.add(  0, -1,  0 ) );
				openset.add( tPos.add(  0,  0, -1 ) );
				openset.add( tPos.add(  1,  0,  0 ) );
				openset.add( tPos.add(  0,  1,  0 ) );
				openset.add( tPos.add(  0,  0,  1 ) );

				if( ShipWright.instance.modConfig.connectDiagonalBlocks1 )
				{
					openset.add( tPos.add( -1, -1,  0 ) );
					openset.add( tPos.add(  1, -1,  0 ) );
					openset.add( tPos.add(  1,  1,  0 ) );
					openset.add( tPos.add( -1,  1,  0 ) );

					openset.add( tPos.add( -1,  0, -1 ) );
					openset.add( tPos.add(  1,  0, -1 ) );
					openset.add( tPos.add(  1,  0,  1 ) );
					openset.add( tPos.add( -1,  0,  1 ) );

					openset.add( tPos.add(  0, -1, -1 ) );
					openset.add( tPos.add(  0,  1, -1 ) );
					openset.add( tPos.add(  0,  1,  1 ) );
					openset.add( tPos.add(  0, -1,  1 ) );
				}
			}
		}
	}

	private void assembleRecursive( AssembleResult result, HashSet< BlockPos > set, BlockPos sPos ) throws ShipSizeOverflowException
	{
		if( result.assembledBlocks.size() > maxBlocks )
			throw new ShipSizeOverflowException();

		final BlockPos pos = sPos;
		if( set.contains( pos ) )
			return;

		set.add( pos );
		final IBlockState blockState = worldObj.getBlockState( pos );
		final Block block = blockState.getBlock();
		if( !canUseBlockForVehicle( block, sPos ) )
			return;

		final LocatedBlock lb = new LocatedBlock( block, worldObj.getBlockState( sPos ), worldObj.getTileEntity( sPos ), pos );
		result.assembleBlock( lb );
		if( block == ShipBlocks.helm && result.shipMarkingBlock == null )
			result.shipMarkingBlock = lb;

		assembleRecursive( result, set, sPos.add( -1,  0,  0 ) );
		assembleRecursive( result, set, sPos.add(  0, -1,  0 ) );
		assembleRecursive( result, set, sPos.add(  0,  0, -1 ) );
		assembleRecursive( result, set, sPos.add(  1,  0,  0 ) );
		assembleRecursive( result, set, sPos.add(  0,  1,  0 ) );
		assembleRecursive( result, set, sPos.add(  0,  0,  1 ) );

		if( ShipWright.instance.modConfig.connectDiagonalBlocks1 )
		{
			assembleRecursive( result, set, sPos.add( -1, -1,  0 ) );
			assembleRecursive( result, set, sPos.add(  1, -1,  0 ) );
			assembleRecursive( result, set, sPos.add(  1,  1,  0 ) );
			assembleRecursive( result, set, sPos.add( -1,  1,  0 ) );

			assembleRecursive( result, set, sPos.add( -1,  0, -1 ) );
			assembleRecursive( result, set, sPos.add(  1,  0, -1 ) );
			assembleRecursive( result, set, sPos.add(  1,  0,  1 ) );
			assembleRecursive( result, set, sPos.add( -1,  0,  1 ) );

			assembleRecursive( result, set, sPos.add(  0, -1, -1 ) );
			assembleRecursive( result, set, sPos.add(  0,  1, -1 ) );
			assembleRecursive( result, set, sPos.add(  0,  1,  1 ) );
			assembleRecursive( result, set, sPos.add(  0, -1,  1 ) );
		}
	}

	public boolean canUseBlockForVehicle( Block block, BlockPos sPos )
	{
		return !block.isAir( worldObj, sPos ) && !block.getMaterial().isLiquid() && block != ShipBlocks.buffer
				&& ShipWright.instance.modConfig.isBlockAllowed( block );
	}
}
