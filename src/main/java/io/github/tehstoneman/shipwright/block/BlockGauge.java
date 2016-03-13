package io.github.tehstoneman.shipwright.block;

import io.github.tehstoneman.shipwright.api.IBlockDensity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.Interface(iface="io.gethub.tehstoneman.shipapi.BlockDensity", modid="shipwright", striprefs=true)
public class BlockGauge extends Block implements IBlockDensity
{
	public static final PropertyDirection	FACING	= PropertyDirection.create( "facing", EnumFacing.Plane.HORIZONTAL );
	public static final PropertyEnum		TYPE	= PropertyEnum.create( "type", EnumType.class );
	public static int						renderID;
	private static String					name	= "gauge";
	public static Object	gaugeBlockRenderID;

	public BlockGauge()
	{
		super( Material.glass );
		setBlockBounds( 0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F );
		setHardness( 1F );
		setResistance( 1F );
		setStepSound( Block.soundTypeMetal );
	}

	public static String getName()
	{
		return name;
	}

	@SideOnly( Side.CLIENT )
	@Override
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
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
	public int damageDropped( IBlockState state )
	{
		EnumType enumType = (EnumType)state.getValue( TYPE );
		return enumType.getMetadata();
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void getSubBlocks( Item item, CreativeTabs tab, List list )
	{
		EnumType[] allTypes = EnumType.values();
		for( EnumType type : allTypes )
			list.add(  new ItemStack( item, 1, type.getMetadata() ) );
	}
	
	@Override
	public IBlockState getStateFromMeta( int meta )
	{
		EnumFacing facing = EnumFacing.getHorizontal( meta );
		int typeBits = ( meta & 0x0c ) >> 2;
		EnumType type = EnumType.byMetadata( typeBits );
		return this.getDefaultState().withProperty( TYPE, type ).withProperty( FACING, facing );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		final EnumFacing facing = (EnumFacing) state.getValue( FACING );
		final EnumType   type   =   (EnumType) state.getValue( TYPE );
		
		int facingBits = facing.getHorizontalIndex();
		int typeBits = type.getMetadata() << 2;
		return facingBits | typeBits;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState( this, new IProperty[] { FACING, TYPE } );
	}

	@Override
	public IBlockState onBlockPlaced( World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer )
	{
		EnumType type = EnumType.byMetadata( meta );
		EnumFacing enumFacing = ( placer == null ) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite();
		return getDefaultState().withProperty( FACING, enumFacing ).withProperty( TYPE, type );
	}

	public static enum EnumType implements IStringSerializable
	{
		BASIC( 0, "basic" ), ADVANCED( 1, "advanced" );

		private final int				meta;
		private final String			name;
		private static final EnumType[]	META_LOOKUP	= new EnumType[values().length];

		private EnumType( int i_meta, String i_name )
		{
			meta = i_meta;
			name = i_name;
		}

		public int getMetadata()
		{
			return meta;
		}

		@Override
		public String toString()
		{
			return name;
		}

		public static EnumType byMetadata( int meta )
		{
			if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
			return META_LOOKUP[meta];
		}

		@Override
		public String getName()
		{
			return name;
		}

		static
		{
			for (final EnumType type : values())
				META_LOOKUP[type.getMetadata()] = type;
		}
	}

	@Method(modid="shipwright")
	@Override
	public int getDensity()
	{
		// Does not contribute to overall density
		return 0;
	}
}
