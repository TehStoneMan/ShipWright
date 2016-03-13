package io.github.tehstoneman.shipwright.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.Fluid;

/**
 * Central access point for all ShipWright API calls
 *
 * @author TehStoneMan
 *
 */
public class ShipWrightAPI
{
	/**
	 * Return the relative density of a given block.
	 * Density is assumed to be on the same scale as Forge Fluid.
	 *
	 * Air = 0, Water = 1000
	 */
	public static int getDensity( Block block )
	{
		if( block instanceof IBlockDensity )
			return ( (IBlockDensity)block ).getDensity();

		// Calculate the density of a block that does not implement BlockDensity

		final Material material = block.getMaterial();

		// Default density of wood
		int density = 700;

		if( IBlockDensity.materialDensityMap.containsKey( material ) )
			density = IBlockDensity.materialDensityMap.get( material );

		if( IBlockDensity.blockDensityMap.containsKey( block.getUnlocalizedName() ) )
			density = IBlockDensity.blockDensityMap.get( block.getUnlocalizedName() );

		if( block instanceof BlockSlab && !( (BlockSlab)block ).isDouble() )
			density = (int)Math.round( density * 0.5 );

		if( block instanceof BlockStairs )
			density = (int)Math.round( density * 0.75 );

		return density;
	}

	/**
	 * Add a material density to the density list
	 */
	public static void addDensity( Material material, int density )
	{
		IBlockDensity.materialDensityMap.put( material, density );
	}

	/**
	 * Add a block density to the density list
	 */
	public static void addDensity( Block block, int density )
	{
		IBlockDensity.blockDensityMap.put( block.getUnlocalizedName(), density );
	}

	/**
	 * Calculate the buoyancy of an object in air
	 */
	public static double getBuoyancyInAir( int density )
	{
		return density * 1.5;
	}

	/**
	 * Calculate the buoyancy of an object in a fluid
	 */
	public static double getBuoyancyInFluid( int density, Fluid fluid )
	{
		return (double)density / (double)fluid.getDensity();
	}

	/**
	 * Return the thrust vector provided by an engine, related to the direction
	 * it is facing
	 */
	public static Vec3 getThrustVector( Block block )
	{
		if( block instanceof ThrustEngine )
			return ( (ThrustEngine)block ).getThrustVector();

		return new Vec3( 0.0, 0.0, 0.0 );
	}
}
