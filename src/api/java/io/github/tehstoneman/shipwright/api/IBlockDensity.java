package io.github.tehstoneman.shipwright.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

/**
 * Describes the density of a block.
 * Implement this to be able to assign a density to your block.
 * 
 * @author TehStoneMan
 *
 */
public interface IBlockDensity
{
	/**
	 * A list of densities for vanilla materials
	 */
	public static Map< Material, Integer >	materialDensityMap	= new HashMap< Material, Integer >()
		{{
			put( Material.air,				   0 );
			put( Material.anvil,			9000 );
			put( Material.barrier,			 600 );
			put( Material.cactus,			 415 );
			put( Material.cake,				 226 );
			put( Material.carpet,			  12 );
			put( Material.circuits,			   8 );
			put( Material.clay,				1760 );
			put( Material.cloth,			 130 );
			put( Material.coral,			 500 );
			put( Material.craftedSnow,		 300 );
			put( Material.dragonEgg,		1600 );
			put( Material.fire,				-100 );
			put( Material.glass,			2000 );
			put( Material.gourd,			 800 );
			put( Material.grass,			1260 );
			put( Material.ground,			1250 );
			put( Material.ice,				 916 );
			put( Material.iron,				7870 );
			put( Material.lava,				3000 );
			put( Material.leaves,			  80 );
			put( Material.packedIce,		 916 );
			put( Material.piston,			1240 );
			put( Material.plants,			 250 );
			put( Material.portal,			   1 );
			put( Material.redstoneLight,	 900 );
			put( Material.rock,				2480 );
			put( Material.sand,				1600 );
			put( Material.snow,				 300 );
			put( Material.sponge,			 115 );
			put( Material.tnt,				 165 );
			put( Material.vine,				  26 );
			put( Material.water,			1000 );
			put( Material.web,				  -1 );
			put( Material.wood,				 700 );
		}};

		/**
		 * A list of materials for various vanilla blocks
		 */
	public static Map<String, Integer>	blockDensityMap		= new HashMap<String, Integer>()
		{{
			put( Blocks.bedrock.getUnlocalizedName(),		100000 );	
			put( Blocks.brewing_stand.getUnlocalizedName(),   1100 );	
			put( Blocks.ladder.getUnlocalizedName(),		   850 );	
			put( Blocks.obsidian.getUnlocalizedName(),		 10000 );
			put( Blocks.torch.getUnlocalizedName(),		       100 );	
		}};
	
	/**
	 * Returns the density of this block
	 * 
	 * @return
	 */
	public int getDensity();
}
