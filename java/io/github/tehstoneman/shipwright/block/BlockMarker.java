package io.github.tehstoneman.shipwright.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockMarker extends Block
{
	public static final PropertyDirection	FACING	= PropertyDirection.create( "facing", EnumFacing.Plane.HORIZONTAL );
	public static int						renderID;
	private static String					name	= "marker";

	public BlockMarker()
	{
		super( Material.wood );
		setCreativeTab( CreativeTabs.tabTransport );
		setHardness( 1F );
		setResistance( 1F );
		setStepSound( Block.soundTypeWood );
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

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState( this, new IProperty[] { FACING } );
	}
	
	@Override
	public int getMetaFromState( IBlockState state )
	{
		EnumFacing facing = (EnumFacing)state.getValue( FACING );
		return facing.getHorizontalIndex();
	}

	@Override
	public IBlockState onBlockPlaced( World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer )
	{
		return getDefaultState().withProperty( FACING, placer.getHorizontalFacing().getOpposite() );
	}
}
