package io.github.tehstoneman.shipwright.proxies;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.BlockBalloon;
import io.github.tehstoneman.shipwright.block.BlockBuffer;
import io.github.tehstoneman.shipwright.block.BlockCrateWood;
import io.github.tehstoneman.shipwright.block.BlockEngine;
import io.github.tehstoneman.shipwright.block.BlockFloater;
import io.github.tehstoneman.shipwright.block.BlockGauge;
import io.github.tehstoneman.shipwright.block.BlockHelm;
import io.github.tehstoneman.shipwright.block.BlockSeat;
import io.github.tehstoneman.shipwright.client.render.ShipModels;
import io.github.tehstoneman.shipwright.client.render.RenderParachute;
import io.github.tehstoneman.shipwright.client.render.RenderShip;
import io.github.tehstoneman.shipwright.client.render.TileEntityGaugeRenderer;
import io.github.tehstoneman.shipwright.control.ShipKeyHandler;
import io.github.tehstoneman.shipwright.entity.EntityParachute;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.items.ItemWheel;
import io.github.tehstoneman.shipwright.items.ShipItems;
import io.github.tehstoneman.shipwright.tileentity.TileEntityGauge;
import io.github.tehstoneman.shipwright.util.ClientHookContainer;
import io.github.tehstoneman.shipwright.util.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{
	public ShipKeyHandler	shipKeyHandler;

	@Override
	public void preInit()
	{
		super.preInit();

		// Add block variants to inventory
		final Item itemBalloon = GameRegistry.findItem( ModInfo.MODID, "balloon" );
		final Item itemGauge   = GameRegistry.findItem( ModInfo.MODID, "gauge" );

		ModelBakery.addVariantName( itemBalloon, ShipWright.modAsset( "balloon_white" ),
												 ShipWright.modAsset( "balloon_orange" ),
												 ShipWright.modAsset( "balloon_magenta" ),
												 ShipWright.modAsset( "balloon_light_blue" ),
												 ShipWright.modAsset( "balloon_yellow" ),
												 ShipWright.modAsset( "balloon_lime" ),
												 ShipWright.modAsset( "balloon_pink" ),
												 ShipWright.modAsset( "balloon_gray" ),
												 ShipWright.modAsset( "balloon_silver" ),
												 ShipWright.modAsset( "balloon_cyan" ),
												 ShipWright.modAsset( "balloon_purple" ),
												 ShipWright.modAsset( "balloon_blue" ),
												 ShipWright.modAsset( "balloon_brown" ),
												 ShipWright.modAsset( "balloon_green" ),
												 ShipWright.modAsset( "balloon_red" ),
												 ShipWright.modAsset( "balloon_black" ) );
		ModelBakery.addVariantName( itemGauge,   ShipWright.modAsset( "gauge_basic" ), ShipWright.modAsset( "gauge_advanced" ) );
	}

	@Override
	public void init()
	{
		super.init();

		ShipModels.Register();
	}

	@Override
	public void postInit()
	{
		super.postInit();
		// TODO Auto-generated method stub
	}

	@Override
	public ClientHookContainer getHookContainer()
	{
		return new ClientHookContainer();
	}

	@Override
	public void registerKeyHandlers( ModSettings cfg )
	{
		shipKeyHandler = new ShipKeyHandler( cfg );
		FMLCommonHandler.instance().bus().register( shipKeyHandler );
	}

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler( EntityShip.class, new RenderShip( null ) );
		RenderingRegistry.registerEntityRenderingHandler( EntityParachute.class, new RenderParachute( null ) );
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityGauge.class, new TileEntityGaugeRenderer() );
		// BlockGauge.gaugeBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
		// BlockSeat.seatBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
		// RenderingRegistry.registerBlockHandler(BlockSeat.seatBlockRenderID, new RenderBlockSeat());
		// RenderingRegistry.registerBlockHandler(BlockGauge.gaugeBlockRenderID, new RenderBlockGauge());
	}
}
