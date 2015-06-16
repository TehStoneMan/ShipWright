package io.github.tehstoneman.shipwright.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockFloater extends Block
{
	private static String	name	= "floater";

	protected BlockFloater()
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

}
