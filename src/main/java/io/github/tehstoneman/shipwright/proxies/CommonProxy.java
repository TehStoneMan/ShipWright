package io.github.tehstoneman.shipwright.proxies;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.ShipBlocks;
import io.github.tehstoneman.shipwright.client.gui.ASGuiHandler;
import io.github.tehstoneman.shipwright.inventory.ShipTab;
import io.github.tehstoneman.shipwright.items.ShipItems;
import io.github.tehstoneman.shipwright.network.ASMessagePipeline;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
	}

	public void init()
	{
		// Recipes
		IRecipe wheelRecipe = new ShapedOreRecipe( new ItemStack( ShipItems.wheel ), new Object[] {
			"/P/",
			"PIP",
			"/P/", '/', "stickWood",
				   'P', "plankWood",
				   'I',	"ingotIron" } );
		GameRegistry.addRecipe( wheelRecipe );

		IRecipe helmRecipe = new ShapedOreRecipe( new ItemStack( ShipBlocks.helm ), new Object[] {
			"PIP",
			"IWI",
			"PRP", 'W', ShipItems.wheel,
				   'P', "plankWood",
				   'I',	"ingotIron",
				   'R',	"dustRedstone" } );
		GameRegistry.addRecipe( helmRecipe );
}

	public void postInit()
	{
		// TODO Auto-generated method stub
	}

	public CommonHookContainer getHookContainer()
	{
		return new CommonHookContainer();
	}

	public void registerPackets( ASMessagePipeline pipeline )
	{
		pipeline.registerPacket( MsgClientHelmAction.class );
		pipeline.registerPacket( MsgClientShipAction.class );
		pipeline.registerPacket( MsgClientRenameShip.class );
		pipeline.registerPacket( MsgClientOpenGUI.class );
		pipeline.registerPacket( MsgAssembleResult.class );
		pipeline.registerPacket( MsgChunkBlockUpdate.class );
		pipeline.registerPacket( MsgRequestShipData.class );
		pipeline.registerPacket( MsgTileEntities.class );
		pipeline.registerPacket( MsgControlInput.class );
		pipeline.registerPacket( MsgFarInteract.class );
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
