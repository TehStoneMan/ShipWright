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
	private World	worldObj;
	public final int	startX, startY, startZ;
	
	private final int	maxBlocks;
	
	public ChunkAssembler(World world, int x, int y, int z)
	{
		worldObj = world;
		
		startX = x;
		startY = y;
		startZ = z;
		
		maxBlocks = ShipWright.instance.modConfig.maxShipChunkBlocks;
	}
	
	public AssembleResult doAssemble()
	{
		AssembleResult result = new AssembleResult();
		result.xOffset = startX;
		result.yOffset = startY;
		result.zOffset = startZ;
		try
		{
			if (ShipWright.instance.modConfig.useNewAlgorithm)
			{
				assembleIterative(result, startX, startY, startZ);
			} else
			{
				assembleRecursive(result, new HashSet<BlockPos>(), startX, startY, startZ);
			}
			if (result.shipMarkingBlock == null)
			{
				result.resultCode = AssembleResult.RESULT_MISSING_MARKER;
			} else
			{
				result.resultCode = AssembleResult.RESULT_OK;
			}
		} catch (ShipSizeOverflowException e)
		{
			result.resultCode = AssembleResult.RESULT_BLOCK_OVERFLOW;
		} catch (Error e)
		{
			LogManager.getLogger( ModInfo.MODID ).error("Error while compiling ship", e);
			result.resultCode = AssembleResult.RESULT_ERROR_OCCURED;
		}
		return result;
	}
	
	private void assembleIterative(AssembleResult result, int sx, int sy, int sz) throws ShipSizeOverflowException
	{
		HashSet<BlockPos> openset = new HashSet<BlockPos>();
		HashSet<BlockPos> closedset = new HashSet<BlockPos>();
		List<BlockPos> iterator = new ArrayList<BlockPos>();
		
		int x = sx, y = sy, z = sz;
		
		openset.add(new BlockPos(sx, sy, sz));
		while (!openset.isEmpty())
		{
			iterator.addAll(openset);
			for (BlockPos pos : iterator)
			{
				openset.remove(pos);
				
				if (closedset.contains(pos))
				{
					continue;
				}
				if (result.assembledBlocks.size() > maxBlocks)
				{
					throw new ShipSizeOverflowException();
				}
				
				x = pos.getX();
				y = pos.getY();
				z = pos.getZ();
				
				closedset.add(pos);
				
				IBlockState blockState = worldObj.getBlockState( pos );
				Block block = blockState.getBlock();
				if (!canUseBlockForVehicle(block, x, y, z))
				{
					continue;
				}
				
				LocatedBlock lb = new LocatedBlock(block, worldObj.getBlockState(new BlockPos(x, y, z)), worldObj.getTileEntity(new BlockPos(x, y, z)), pos);
				result.assembleBlock(lb);
				if (block == ShipBlocks.helm && result.shipMarkingBlock == null)
				{
					result.shipMarkingBlock = lb;
				}
				
				openset.add(new BlockPos(x - 1, y, z));
				openset.add(new BlockPos(x, y - 1, z));
				openset.add(new BlockPos(x, y, z - 1));
				openset.add(new BlockPos(x + 1, y, z));
				openset.add(new BlockPos(x, y + 1, z));
				openset.add(new BlockPos(x, y, z + 1));
				
				if (ShipWright.instance.modConfig.connectDiagonalBlocks1)
				{
					openset.add(new BlockPos(x - 1, y - 1, z));
					openset.add(new BlockPos(x + 1, y - 1, z));
					openset.add(new BlockPos(x + 1, y + 1, z));
					openset.add(new BlockPos(x - 1, y + 1, z));
					
					openset.add(new BlockPos(x - 1, y, z - 1));
					openset.add(new BlockPos(x + 1, y, z - 1));
					openset.add(new BlockPos(x + 1, y, z + 1));
					openset.add(new BlockPos(x - 1, y, z + 1));
					
					openset.add(new BlockPos(x, y - 1, z - 1));
					openset.add(new BlockPos(x, y + 1, z - 1));
					openset.add(new BlockPos(x, y + 1, z + 1));
					openset.add(new BlockPos(x, y - 1, z + 1));
				}
			}
		}
	}
	
	private void assembleRecursive(AssembleResult result, HashSet<BlockPos> set, int x, int y, int z) throws ShipSizeOverflowException
	{
		if (result.assembledBlocks.size() > maxBlocks)
		{
			throw new ShipSizeOverflowException();
		}
		
		BlockPos pos = new BlockPos(x, y, z);
		if (set.contains(pos)) return;
		
		set.add(pos);
		IBlockState blockState = worldObj.getBlockState( pos );
		Block block =blockState.getBlock();
		if (!canUseBlockForVehicle(block, x, y, z)) return;
		
		LocatedBlock lb = new LocatedBlock(block, worldObj.getBlockState(new BlockPos(x, y, z)), worldObj.getTileEntity(new BlockPos(x, y, z)), pos);
		result.assembleBlock(lb);
		if (block == ShipBlocks.helm && result.shipMarkingBlock == null)
		{
			result.shipMarkingBlock = lb;
		}
		
		assembleRecursive(result, set, x - 1, y, z);
		assembleRecursive(result, set, x, y - 1, z);
		assembleRecursive(result, set, x, y, z - 1);
		assembleRecursive(result, set, x + 1, y, z);
		assembleRecursive(result, set, x, y + 1, z);
		assembleRecursive(result, set, x, y, z + 1);
		
		if (ShipWright.instance.modConfig.connectDiagonalBlocks1)
		{
			assembleRecursive(result, set, x - 1, y - 1, z);
			assembleRecursive(result, set, x + 1, y - 1, z);
			assembleRecursive(result, set, x + 1, y + 1, z);
			assembleRecursive(result, set, x - 1, y + 1, z);
			
			assembleRecursive(result, set, x - 1, y, z - 1);
			assembleRecursive(result, set, x + 1, y, z - 1);
			assembleRecursive(result, set, x + 1, y, z + 1);
			assembleRecursive(result, set, x - 1, y, z + 1);
			
			assembleRecursive(result, set, x, y - 1, z - 1);
			assembleRecursive(result, set, x, y + 1, z - 1);
			assembleRecursive(result, set, x, y + 1, z + 1);
			assembleRecursive(result, set, x, y - 1, z + 1);
		}
	}
	
	public boolean canUseBlockForVehicle(Block block, int x, int y, int z)
	{
		return !block.isAir(worldObj, new BlockPos(x, y, z)) && !block.getMaterial().isLiquid() && block != ShipBlocks.buffer && ShipWright.instance.modConfig.isBlockAllowed(block);
	}
}
