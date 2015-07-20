package io.github.tehstoneman.shipwright.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class LocatedBlock
{
	public final Block			block;
	public final IBlockState	blockMeta;
	public final TileEntity		tileEntity;
	public final BlockPos	coords;
	
	public LocatedBlock(Block block, IBlockState blockState, BlockPos coords)
	{
		this(block, blockState, null, coords);
	}
	
	public LocatedBlock(Block block, IBlockState blockState, TileEntity tileentity, BlockPos coords)
	{
		this.block = block;
		blockMeta = blockState;
		tileEntity = tileentity;
		this.coords = coords;
	}
	
	public LocatedBlock(NBTTagCompound comp, World world)
	{
		block = Block.getBlockById(comp.getInteger("block"));
		blockMeta = null;
		coords = new BlockPos(comp.getInteger("x"), comp.getInteger("y"), comp.getInteger("z"));
		tileEntity = world == null ? null : world.getTileEntity(coords);
	}
	
	@Override
	public String toString()
	{
		return new StringBuilder("LocatedBlock [block=").append(block).append(", meta=").append(blockMeta).append(", coords=[").append(coords.getX()).append(", ").append(coords.getY()).append(", ").append(coords.getZ()).append("]]").toString();
	}
	
	public void writeToNBT(NBTTagCompound comp)
	{
		comp.setShort("block", (short) Block.getIdFromBlock(block));
		//comp.setInteger("meta", blockMeta);
		comp.setInteger("x", coords.getX());
		comp.setInteger("y", coords.getY());
		comp.setInteger("z", coords.getZ());
	}
}
