package io.github.tehstoneman.shipwright.block;

import io.github.tehstoneman.shipapi.BlockDensity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;

@Optional.Interface(iface="io.gethub.tehstoneman.shipapi.BlockDensity", modid="shipwright", striprefs=true)
public class BlockFloater extends Block implements BlockDensity
{
	private static String	name	= "floater";

	protected BlockFloater()
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

	@Method(modid="shipwright")
	@Override
	public int getDensity()
	{
		// This block should be much lighter than water
		return 10;
	}

}
