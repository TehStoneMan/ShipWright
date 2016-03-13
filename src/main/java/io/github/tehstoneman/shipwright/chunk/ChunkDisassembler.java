package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.ShipBlocks;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.entity.IShipTileEntity;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.github.tehstoneman.shipwright.util.MathHelperMod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;

public class ChunkDisassembler
{
	private final EntityShip	ship;
	public boolean				overwrite;

	public ChunkDisassembler( EntityShip entityship )
	{
		ship = entityship;
		overwrite = false;
	}

	public boolean canDisassemble()
	{
		if( overwrite )
			return true;
		final World world = ship.worldObj;
		final MobileChunk chunk = ship.getShipChunk();
		float yaw = Math.round( ship.rotationYaw / 90F ) * 90F;
		yaw = (float)Math.toRadians( yaw );

		final float ox = -chunk.getCenterX();
		final float oy = -chunk.minY(); // Created the normal way, through a
										// VehicleFiller, this value will always
										// be 0.
		final float oz = -chunk.getCenterZ();

		Vec3 vec = new Vec3( 0D, 0D, 0D );
		Block block;
		IBlockState blockState;
		int ix, iy, iz;
		for( int i = chunk.minX(); i < chunk.maxX(); i++ )
			for( int j = chunk.minY(); j < chunk.maxY(); j++ )
				for( int k = chunk.minZ(); k < chunk.maxZ(); k++ )
				{
					if( chunk.isAirBlock( new BlockPos(i, j, k )) )
						continue;
					vec = new Vec3( i + ox, j + oy, k + oz );
					vec.rotateYaw( yaw );

					ix = MathHelperMod.round_double( vec.xCoord + ship.posX );
					iy = MathHelperMod.round_double( vec.yCoord + ship.posY );
					iz = MathHelperMod.round_double( vec.zCoord + ship.posZ );

					blockState = world.getBlockState( new BlockPos( ix, iy, iz ) );
					block = blockState.getBlock();
					if( block != null && !block.isAir( world, new BlockPos( ix, iy, iz ) ) && !block.getMaterial().isLiquid()
							&& !ShipWright.instance.modConfig.overwritableBlocks.contains( block ) )
						return false;
				}
		return true;
	}

	public AssembleResultOld doDisassemble()
	{
		final World world = ship.worldObj;
		final MobileChunk chunk = ship.getShipChunk();
		final AssembleResultOld result = new AssembleResultOld();
		result.offsetPos = new BlockPos( Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

		final int currentrot = Math.round( ship.rotationYaw / 90F ) & 3;
		final int deltarot = -currentrot & 3;
		ship.rotationYaw = currentrot * 90F;
		ship.rotationPitch = 0F;
		final float yaw = currentrot * MathHelperMod.PI_HALF;

		final boolean flag = world.getGameRules().getBoolean( "doTileDrops" );
		world.getGameRules().setOrCreateGameRule( "doTileDrops", "false" );

		final List< LocatedBlock > postlist = new ArrayList< LocatedBlock >( 4 );

		final float ox = -chunk.getCenterX();
		final float oy = -chunk.minY(); // Created the normal way, through a
										// ChunkAssembler, this value will
										// always be 0.
		final float oz = -chunk.getCenterZ();

		Vec3 vec = new Vec3( 0D, 0D, 0D );
		TileEntity tileentity;
		Block block;
		IBlockState blockState;
		int ix, iy, iz;
		for( int i = chunk.minX(); i < chunk.maxX(); i++ )
			for( int j = chunk.minY(); j < chunk.maxY(); j++ )
				for( int k = chunk.minZ(); k < chunk.maxZ(); k++ )
				{
					blockState = chunk.getBlockState( new BlockPos(i, j, k ));
					block = blockState.getBlock();
					if( block.isAir( world, new BlockPos( i, j, k ) ) )
						continue;
					tileentity = chunk.getTileEntity( new BlockPos(i, j, k ));

					//blockState = ShipWright.instance.metaRotations.getRotatedMeta( block, blockState, deltarot );

					vec = new Vec3( i + ox, j + oy, k + oz );
					vec.rotateYaw( yaw );

					ix = MathHelperMod.round_double( vec.xCoord + ship.posX );
					iy = MathHelperMod.round_double( vec.yCoord + ship.posY );
					iz = MathHelperMod.round_double( vec.zCoord + ship.posZ );

					if( !world.setBlockState( new BlockPos( ix, iy, iz ), blockState, 2 )
							|| block != world.getBlockState( new BlockPos( ix, iy, iz ) ).getBlock() )
					{
						postlist.add( new LocatedBlock( block, blockState, tileentity, new BlockPos( ix, iy, iz ) ) );
						continue;
					}
					if( blockState != world.getBlockState( new BlockPos( ix, iy, iz ) ) )
						world.setBlockState( new BlockPos( ix, iy, iz ), blockState, 2 );
					if( tileentity != null )
					{
						if( tileentity instanceof IShipTileEntity )
							( (IShipTileEntity)tileentity ).setParentShip( null, i, j, k );
						tileentity.validate();
						world.setTileEntity( new BlockPos( ix, iy, iz ), tileentity );
					}

					/*
					if( !ShipWright.instance.metaRotations.hasBlock( block ) )
					{
						// ShipMod.modLog.debug("Forge-rotating block " +
						// Block.blockRegistry.getNameForObject(block));
						rotateBlock( block, world, ix, iy, iz, currentrot );
						blockState = world.getBlockState( new BlockPos( ix, iy, iz ) );
						block = blockState.getBlock();
						tileentity = world.getTileEntity( new BlockPos( ix, iy, iz ) );
					}
					*/

					final LocatedBlock lb = new LocatedBlock( block, blockState, tileentity, new BlockPos( ix, iy, iz ) );
					result.assembleBlock( lb );
					if( block == ShipBlocks.helm && i == ship.seatX && j == ship.seatY && k == ship.seatZ )
						result.shipMarkingBlock = lb;
				}

		world.getGameRules().setOrCreateGameRule( "doTileDrops", String.valueOf( flag ) );

		for( final LocatedBlock ilb : postlist )
		{
			ix = ilb.coords.getX();
			iy = ilb.coords.getY();
			iz = ilb.coords.getZ();
			LogManager.getLogger( ModInfo.MODID ).debug( "Post-rejoining block: " + ilb.toString() );
			world.setBlockState( new BlockPos( ix, iy, iz ), ilb.blockMeta, 0 );
			result.assembleBlock( ilb );
		}

		ship.setDead();

		if( result.shipMarkingBlock == null || !( result.shipMarkingBlock.tileEntity instanceof TileEntityHelm ) )
			result.resultCode = AssembleResultOld.RESULT_MISSING_MARKER;
		else
			result.checkConsistent( world );
		return result;
	}

	private void rotateBlock( Block block, World world, int x, int y, int z, int deltarot )
	{
		deltarot &= 3;
		if( deltarot != 0 )
			if( deltarot == 3 )
				block.rotateBlock( world, new BlockPos( x, y, z ), EnumFacing.UP );
			else
				for( int r = 0; r < deltarot; r++ )
					block.rotateBlock( world, new BlockPos( x, y, z ), EnumFacing.DOWN );
	}
}
