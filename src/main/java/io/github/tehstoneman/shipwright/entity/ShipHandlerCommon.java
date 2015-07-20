package io.github.tehstoneman.shipwright.entity;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.chunk.MobileChunk;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;

public abstract class ShipHandlerCommon
{
	public final EntityShip	ship;
	
	public ShipHandlerCommon(EntityShip entityship)
	{
		ship = entityship;
	}
	
	public boolean interact(EntityPlayer player)
	{
		return false;
	}
	
	public void onChunkUpdate()
	{
		MobileChunk chunk = ship.getShipChunk();
		ship.getCapabilities().clearBlockCount();
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					IBlockState blockState = chunk.getBlockState(new BlockPos(i, j, k));
					Block block = blockState.getBlock();
					if (block.getMaterial() != Material.air)
					{
						ship.getCapabilities().onChunkBlockAdded(block, blockState, i, j, k);
					}
				}
			}
		}
		
		ship.setSize(Math.max(chunk.maxX() - chunk.minX(), chunk.maxZ() - chunk.minZ()), chunk.maxY() - chunk.minY());
		World.MAX_ENTITY_RADIUS = Math.max(World.MAX_ENTITY_RADIUS, Math.max(ship.width, ship.height) + 2F);
		
		try
		{
			ship.fillAirBlocks(new HashSet<BlockPos>(), -1, -1, -1);
		} catch (StackOverflowError e)
		{
			LogManager.getLogger( ModInfo.MODID ).error("Failure during ship post-initialization", e);
		}
		
		ship.layeredBlockVolumeCount = new int[chunk.maxY() - chunk.minY()];
		for (int y = 0; y < ship.layeredBlockVolumeCount.length; y++)
		{
			for (int i = chunk.minX(); i < chunk.maxX(); i++)
			{
				for (int j = chunk.minZ(); j < chunk.maxZ(); j++)
				{
					if (chunk.isBlockTakingWaterVolume(new BlockPos(i, y + chunk.minY(), j)))
					{
						ship.layeredBlockVolumeCount[y]++;
					}
				}
			}
		}
		ship.isFlying = ship.getCapabilities().canFly();
	}
}
