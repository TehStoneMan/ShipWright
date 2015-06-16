package io.github.tehstoneman.shipwright.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBalloon extends Block
{
	public static final PropertyEnum	COLOR	= PropertyEnum.create( "color", EnumDyeColor.class );
	private static String				name	= "balloon";

	public BlockBalloon()
	{
		super( Material.cloth );
		setCreativeTab( CreativeTabs.tabTransport );
		setHardness( 0.35F );
		setResistance( 1F );
		setStepSound( Block.soundTypeCloth );
	}

	public static String getName()
	{
		return name;
	}

	@Override
	public int damageDropped( IBlockState state )
	{
		final EnumDyeColor enumColor = (EnumDyeColor) state.getValue( COLOR );
		return enumColor.getMetadata();
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void getSubBlocks( Item itemIn, CreativeTabs tab, List list )
	{
		final EnumDyeColor[] allColors = EnumDyeColor.values();
		for (final EnumDyeColor color : allColors)
			list.add( new ItemStack( itemIn, 1, color.getMetadata() ) );
	}

	@Override
	public IBlockState getStateFromMeta( int meta )
	{
		return getDefaultState().withProperty( COLOR, EnumDyeColor.byMetadata( meta ) );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		return ((EnumDyeColor) state.getValue( COLOR )).getMetadata();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState( this, new IProperty[] { COLOR } );
	}
}