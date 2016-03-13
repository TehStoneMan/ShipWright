package io.github.tehstoneman.shipwright.proxies;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.ShipBlocks;
import io.github.tehstoneman.shipwright.client.gui.ASGuiHandler;
import io.github.tehstoneman.shipwright.inventory.ShipTab;
import io.github.tehstoneman.shipwright.items.ShipItems;
import io.github.tehstoneman.shipwright.network.MsgAssembleResult;
import io.github.tehstoneman.shipwright.network.MsgChunkBlockUpdate;
import io.github.tehstoneman.shipwright.network.MsgClientHelmAction;
import io.github.tehstoneman.shipwright.network.MsgClientOpenGUI;
import io.github.tehstoneman.shipwright.network.MsgClientRenameShip;
import io.github.tehstoneman.shipwright.network.MsgClientShipAction;
import io.github.tehstoneman.shipwright.network.MsgControlInput;
import io.github.tehstoneman.shipwright.network.MsgFarInteract;
import io.github.tehstoneman.shipwright.network.MsgRequestShipData;
import io.github.tehstoneman.shipwright.network.MsgTileEntities;
import io.github.tehstoneman.shipwright.util.CommonHookContainer;
import io.github.tehstoneman.shipwright.util.CommonPlayerTicker;
import io.github.tehstoneman.shipwright.util.ModSettings;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CommonProxy
{
	public CommonPlayerTicker	playerTicker;
	public CommonHookContainer	hookContainer;
	public static ShipTab		shipTab;

	public void preInit()
	{
		ShipItems.registerItems();
		ShipBlocks.registerBlocks();

		shipTab = new ShipTab( "shipwright" );

		//@formatter:off
		// Initialize network messaging
		byte packetID = 0;
		ShipWright.network = NetworkRegistry.INSTANCE.newSimpleChannel( ModInfo.MODID );
		ShipWright.network.registerMessage( MsgClientHelmAction.Handler.class, MsgClientHelmAction.class, packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgClientShipAction.Handler.class, MsgClientShipAction.class, packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgClientRenameShip.Handler.class, MsgClientRenameShip.class, packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgClientOpenGUI.Handler.class,    MsgClientOpenGUI.class,    packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgAssembleResult.Handler.class,   MsgAssembleResult.class,   packetID++, Side.CLIENT );
		ShipWright.network.registerMessage( MsgChunkBlockUpdate.Handler.class, MsgChunkBlockUpdate.class, packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgRequestShipData.Handler.class,  MsgRequestShipData.class,  packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgTileEntities.Handler.class,     MsgTileEntities.class,     packetID++, Side.CLIENT );
		ShipWright.network.registerMessage( MsgControlInput.Handler.class,     MsgControlInput.class,     packetID++, Side.SERVER );
		ShipWright.network.registerMessage( MsgFarInteract.Handler.class,      MsgFarInteract.class,      packetID++, Side.SERVER );
		//@formatter:on
	}

	public void init()
	{
		final Item ASHelm = GameRegistry.findItem( "ArchimedesShipsPlus", "marker" );
		// Recipes
		if( ASHelm == null )
		{
			final IRecipe wheelRecipe = new ShapedOreRecipe( new ItemStack( ShipItems.wheel ),
					new Object[] { "P/P",
								   "/I/",
								   "P/P", '/', "stickWood",
								   		  'P', "plankWood",
								   		  'I', "ingotIron" } );
			GameRegistry.addRecipe( wheelRecipe );

			final IRecipe helmRecipe = new ShapedOreRecipe( new ItemStack( ShipBlocks.helm ),
					new Object[] { "PPP",
								   "IWI",
								   "BRB", 'W', ShipItems.wheel,
								   		  'P', "plankWood",
								   		  'I', "ingotIron",
								   		  'R', Blocks.piston,
								   		  'B', Items.boat } );
			GameRegistry.addRecipe( helmRecipe );
		}
		else
		{
			// In order to avoid conflicts with Archimedes' Ships Plus mod, if it is installed, replace our ship's wheel with their helm block, as they both use
			// the same crafting recipe
			final IRecipe helmRecipe = new ShapedOreRecipe( new ItemStack( ShipBlocks.helm ),
					new Object[] { "PPP",
								   "IWI",
								   "BRB", 'W', ASHelm,
								   		  'P', "plankWood",
								   		  'I', "ingotIron",
								   		  'R', Blocks.piston,
								   		  'B', Items.boat } );
			GameRegistry.addRecipe( helmRecipe );
		}
	}

	public void postInit()
	{
		// TODO Auto-generated method stub
	}

	public CommonHookContainer getHookContainer()
	{
		return new CommonHookContainer();
	}

	public void registerKeyHandlers( ModSettings cfg )
	{}

	public void registerEventHandlers()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler( ShipWright.instance, new ASGuiHandler() );

		playerTicker = new CommonPlayerTicker();
		FMLCommonHandler.instance().bus().register( playerTicker );
		MinecraftForge.EVENT_BUS.register( hookContainer = getHookContainer() );
	}

	public void registerRenderers()
	{}
}
