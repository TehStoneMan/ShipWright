package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.entity.EntityShip;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MobileChunkServer extends MobileChunk
{
	private Set<BlockPos>	sendQueue;
	
	public MobileChunkServer(World world, EntityShip entityship)
	{
		super(world, entityship);
		sendQueue = new HashSet<BlockPos>();
	}
	
	public Collection<BlockPos> getSendQueue()
	{
		return sendQueue;
	}
	
	@Override
	public boolean setBlockIDWithMetadata(int x, int y, int z, Block block, IBlockState blockState)
	{
		if (super.setBlockIDWithMetadata(x, y, z, block, blockState))
		{
			sendQueue.add(new BlockPos(x, y, z));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setBlockMetadata(int x, int y, int z, IBlockState blockState)
	{
		if (super.setBlockMetadata(x, y, z, blockState))
		{
			sendQueue.add(new BlockPos(x, y, z));
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSetBlockAsFilledAir(int x, int y, int z)
	{
	}
}
