package io.github.tehstoneman.shipwright.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBuffer extends Block
{
	private static String	name	= "buffer";

	protected BlockBuffer()
	{
		super( Material.clay );
		setCreativeTab( CreativeTabs.tabTransport );
		setHardness( 1F );
		setResistance( 1F );
		setStepSound( Block.SLIME_SOUND );
	}

	public static String getName()
	{
		return name;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@SideOnly( Side.CLIENT )
	@Override
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.TRANSLUCENT;
	}

	/*
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        IBlockState nextBlockstate = worldIn.getBlockState(pos.offset(side.getOpposite()));
        Block nextBlock = nextBlockstate.getBlock();
        
        if (block == this)
        {
            return false;
        }

        return super.shouldSideBeRendered(worldIn, pos, side);
    }
    */
}
