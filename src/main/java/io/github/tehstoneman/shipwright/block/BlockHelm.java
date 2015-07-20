package io.github.tehstoneman.shipwright.block;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockHelm extends BlockContainer
{
	public static final PropertyDirection	FACING	= PropertyDirection.create( "facing", EnumFacing.Plane.HORIZONTAL );
	private static String					name	= "marker";

	public BlockHelm()
	{
		super( Material.wood );
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
	public int getRenderType()
    {
        return 3;
    }

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState( this, new IProperty[] { FACING } );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		final EnumFacing facing = (EnumFacing)state.getValue( FACING );
		return facing.getHorizontalIndex();
	}

	@Override
	public IBlockState onBlockPlaced( World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer )
	{
		return getDefaultState().withProperty( FACING, placer.func_174811_aO().getOpposite() );
	}

	@Override
	public boolean onBlockActivated( World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY,
			float hitZ )
	{
		if( !playerIn.isSneaking() )
		{
			final TileEntity tileentity = worldIn.getTileEntity( pos );
			if( tileentity != null )
			{
				playerIn.openGui( ShipWright.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ() );
				return true;
			}
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity( World worldIn, int meta )
	{
		return new TileEntityHelm();
	}
}
