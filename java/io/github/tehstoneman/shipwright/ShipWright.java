package io.github.tehstoneman.shipwright;

import io.github.tehstoneman.shipwright.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

@Mod( modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES, acceptedMinecraftVersions = ModInfo.MINECRAFT )
public class ShipWright
{
	public static ModMetadata	modMetadata;

	@Mod.Instance( value = ModInfo.MODID )
	public static ShipWright	instance;

	public static Logger		logger;

	// Define proxies
	@SidedProxy( clientSide = ModInfo.PROXY_LOCATION + "ClientProxy", serverSide = ModInfo.PROXY_LOCATION + "CommonProxy" )
	public static CommonProxy	proxy;

	@Mod.EventHandler
	public void preInitialize( FMLPreInitializationEvent event )
	{
		logger = event.getModLog();
		proxy.preInit();


		/*
		 * MaterialMap.registerMaterial("air", Material.air);
		 * MaterialMap.registerMaterial("anvil", Material.anvil);
		 * MaterialMap.registerMaterial("cactus", Material.cactus);
		 * MaterialMap.registerMaterial("cake", Material.cake);
		 * MaterialMap.registerMaterial("carpet", Material.carpet);
		 * MaterialMap.registerMaterial("circuits", Material.circuits);
		 * MaterialMap.registerMaterial("clay", Material.clay);
		 * MaterialMap.registerMaterial("cloth", Material.cloth);
		 * MaterialMap.registerMaterial("coral", Material.coral);
		 * MaterialMap.registerMaterial("dragon_egg", Material.dragonEgg);
		 * MaterialMap.registerMaterial("fire", Material.fire);
		 * MaterialMap.registerMaterial("glass", Material.glass);
		 * MaterialMap.registerMaterial("gourd", Material.gourd);
		 * MaterialMap.registerMaterial("grass", Material.grass);
		 * MaterialMap.registerMaterial("ground", Material.ground);
		 * MaterialMap.registerMaterial("ice", Material.ice);
		 * MaterialMap.registerMaterial("ice_packed", Material.packedIce);
		 * MaterialMap.registerMaterial("iron", Material.iron);
		 * MaterialMap.registerMaterial("lava", Material.lava);
		 * MaterialMap.registerMaterial("leaves", Material.leaves);
		 * MaterialMap.registerMaterial("piston", Material.piston);
		 * MaterialMap.registerMaterial("plants", Material.plants);
		 * MaterialMap.registerMaterial("portal", Material.portal);
		 * MaterialMap.registerMaterial("redstone_light",
		 * Material.redstoneLight);
		 * MaterialMap.registerMaterial("rock", Material.rock);
		 * MaterialMap.registerMaterial("sand", Material.sand);
		 * MaterialMap.registerMaterial("snow", Material.snow);
		 * MaterialMap.registerMaterial("snow_crafted", Material.craftedSnow);
		 * MaterialMap.registerMaterial("sponge", Material.sponge);
		 * MaterialMap.registerMaterial("tnt", Material.tnt);
		 * MaterialMap.registerMaterial("vine", Material.vine);
		 * MaterialMap.registerMaterial("water", Material.water);
		 * MaterialMap.registerMaterial("web", Material.web);
		 * MaterialMap.registerMaterial("wood", Material.wood);
		 */

		//modConfig = new Settings( new Configuration( event.getSuggestedConfigurationFile() ) );
		//modConfig.loadAndSave();

		//metaRotations.setConfigDirectory( event.getModConfigurationDirectory() );

		//pipeline.initalize();

		//modConfig.postLoad();
	}

	private void registerBlocksAndItems()
	{
		//GameRegistry.addRecipe( new ItemStack( blockMarkShip, 1 ), "X#X", "#O#", "X#X", Character.valueOf( 'X' ), Blocks.planks, Character.valueOf( '#' ), Items.stick, Character.valueOf( 'O' ), Items.iron_ingot );
		//GameRegistry.registerTileEntity( TileEntityHelm.class, "archiHelm" );
		//Blocks.fire.setFireInfo( blockMarkShip, 5, 5 );

		//GameRegistry.addShapelessRecipe( new ItemStack( blockFloater, 1 ), Blocks.log, Blocks.wool );
		//GameRegistry.addShapelessRecipe( new ItemStack( blockFloater, 1 ), Blocks.log2, Blocks.wool );

		// for (int i = 0; i < ItemDye.dyeColors.length; i++)
		// GameRegistry.addRecipe( new ItemStack( blockBalloon, 1, i ), "X", "#", Character.valueOf( 'X' ), new ItemStack( Blocks.wool, 1, i ), Character.valueOf( '#' ), Items.string );
		// Blocks.fire.setFireInfo( blockBalloon, 30, 60 );

		//GameRegistry.addRecipe( new ItemStack( blockGauge, 1, 0 ), "VXV", "XO#", " # ", Character.valueOf( 'X' ), Items.iron_ingot, Character.valueOf( '#' ), Items.gold_ingot, Character.valueOf( 'O' ), Items.redstone, Character.valueOf( 'V' ), Blocks.glass_pane );
		//GameRegistry.addRecipe( new ItemStack( blockGauge, 1, 0 ), "VXV", "XO#", " # ", Character.valueOf( 'X' ), Items.gold_ingot, Character.valueOf( '#' ), Items.iron_ingot, Character.valueOf( 'O' ), Items.redstone, Character.valueOf( 'V' ), Blocks.glass_pane );
		//GameRegistry.addRecipe( new ItemStack( blockGauge, 1, 1 ), "VXV", "XO#", "V#V", Character.valueOf( 'X' ), Items.iron_ingot, Character.valueOf( '#' ), Items.gold_ingot, Character.valueOf( 'O' ), Items.redstone, Character.valueOf( 'V' ), Blocks.glass_pane );
		//GameRegistry.addRecipe( new ItemStack( blockGauge, 1, 1 ), "VXV", "XO#", "V#V", Character.valueOf( 'X' ), Items.gold_ingot, Character.valueOf( '#' ), Items.iron_ingot, Character.valueOf( 'O' ), Items.redstone, Character.valueOf( 'V' ), Blocks.glass_pane );
		//GameRegistry.registerTileEntity( TileEntityGauge.class, "archiGauge" );

		//GameRegistry.addRecipe( new ItemStack( blockSeat ), "X ", "XX", Character.valueOf( 'X' ), Blocks.wool );
		//Blocks.fire.setFireInfo( blockSeat, 30, 30 );

		//GameRegistry.addShapelessRecipe( new ItemStack( blockBuffer ), blockFloater, new ItemStack( Items.dye, 1, 0 ) );

		//GameRegistry.addRecipe( new ItemStack( blockCrateWood, 3 ), " # ", "# #", "XXX", Character.valueOf( '#' ), Items.leather, Character.valueOf( 'X' ), Blocks.planks );
		//GameRegistry.registerTileEntity( TileEntityCrate.class, "archiCrate" );

		//GameRegistry.addRecipe( new ItemStack( blockEngine, 1 ), "#O#", "#X#", "###", Character.valueOf( '#' ), Items.iron_ingot, Character.valueOf( 'O' ), Items.water_bucket, Character.valueOf( 'X' ), Blocks.furnace );
		//GameRegistry.registerTileEntity( TileEntityEngine.class, "archiEngine" );
	}

	@Mod.EventHandler
	public void initialize( FMLInitializationEvent event )
	{
		proxy.init();

		//registerBlocksAndItems();

		//EntityRegistry.registerModEntity( EntityShip.class, "shipmod", 1, this, 64, modConfig.shipEntitySyncRate, true );
		//EntityRegistry.registerModEntity( EntityEntityAttachment.class, "attachment", 2, this, 64, 100, false );
		//EntityRegistry.registerModEntity( EntitySeat.class, "attachment.seat", 3, this, 64, 100, false );
		//EntityRegistry.registerModEntity( EntityParachute.class, "parachute", 4, this, 32, modConfig.shipEntitySyncRate, true );

		/*
		 * MaterialDensity.addDensity(Material.air, 0F);
		 * MaterialDensity.addDensity(Material.wood, 0.700F);
		 * MaterialDensity.addDensity(Material.wood, 0.500F);
		 * MaterialDensity.addDensity(Material.rock, 2.500F);
		 * MaterialDensity.addDensity(Material.water, 1.000F);
		 * MaterialDensity.addDensity(Material.lava, 2.500F);
		 * MaterialDensity.addDensity(Material.ice, 0.916F);
		 * MaterialDensity.addDensity(Material.iron, 7.874F);
		 * MaterialDensity.addDensity(Material.iron, 5.000F);
		 * MaterialDensity.addDensity(Material.anvil, 5.000F);
		 * MaterialDensity.addDensity(Material.glass, 2.600F);
		 * MaterialDensity.addDensity(Material.glass, 0.400F);
		 * MaterialDensity.addDensity(Material.leaves, 0.200F);
		 * MaterialDensity.addDensity(Material.plants, 0.200F);
		 * MaterialDensity.addDensity(Material.cloth, 1.314F);
		 * MaterialDensity.addDensity(Material.cloth, 0.700F);
		 * MaterialDensity.addDensity(Material.sand, 1.600F);
		 * MaterialDensity.addDensity(Material.ground, 2.000F);
		 * MaterialDensity.addDensity(Material.grass, 2.000F);
		 * MaterialDensity.addDensity(Material.clay, 2.000F);
		 * MaterialDensity.addDensity(Material.gourd, 0.900F);
		 * MaterialDensity.addDensity(Material.sponge, 0.400F);
		 * MaterialDensity.addDensity(Material.craftedSnow, 0.800F);
		 * MaterialDensity.addDensity(Material.tnt, 1.200F);
		 * MaterialDensity.addDensity(Material.piston, 1.000F);
		 * MaterialDensity.addDensity(Material.cloth, 0.100F);
		 * MaterialDensity.addDensity(materialFloater, 0.04F);
		 * MaterialDensity.addDensity(blockBalloon, 0.02F);
		 */

		//proxy.registerKeyHandlers( modConfig );
		//proxy.registerEventHandlers();
		//proxy.registerRenderers();
		//proxy.registerPackets( pipeline );
	}

	@EventHandler
	public void postInitMod( FMLPostInitializationEvent event )
	{
		//metaRotations.readMetaRotationFiles();
		//pipeline.postInitialize();
	}

	/**
	 * Prepend the name with the mod ID, suitable for ResourceLocations such as
	 * textures.
	 * Adapted from MinecraftByExample
	 *
	 * @param name
	 * @return "shipwright:name"
	 */
	public static String modAsset( String name )
	{
		return ModInfo.MODID + ":" + name;
	}
}
