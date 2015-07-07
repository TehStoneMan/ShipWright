package io.github.tehstoneman.shipwright.proxies;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.BlockBalloon;
import io.github.tehstoneman.shipwright.block.BlockBuffer;
import io.github.tehstoneman.shipwright.block.BlockCrateWood;
import io.github.tehstoneman.shipwright.block.BlockEngine;
import io.github.tehstoneman.shipwright.block.BlockFloater;
import io.github.tehstoneman.shipwright.block.BlockGauge;
import io.github.tehstoneman.shipwright.block.BlockMarker;
import io.github.tehstoneman.shipwright.block.BlockSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		super.preInit();

		// Add block variants to inventory
		final Item itemBalloon = GameRegistry.findItem( ModInfo.MODID, "balloon" );
		final Item itemGauge   = GameRegistry.findItem( ModInfo.MODID, "gauge" );

		ModelBakery.addVariantName( itemBalloon, ShipWright.modAsset( "balloon_white" ), ShipWright.modAsset( "balloon_orange" ),
				ShipWright.modAsset( "balloon_magenta" ), ShipWright.modAsset( "balloon_light_blue" ), ShipWright.modAsset( "balloon_yellow" ),
				ShipWright.modAsset( "balloon_lime" ), ShipWright.modAsset( "balloon_pink" ), ShipWright.modAsset( "balloon_gray" ),
				ShipWright.modAsset( "balloon_silver" ), ShipWright.modAsset( "balloon_cyan" ), ShipWright.modAsset( "balloon_purple" ),
				ShipWright.modAsset( "balloon_blue" ), ShipWright.modAsset( "balloon_brown" ), ShipWright.modAsset( "balloon_green" ),
				ShipWright.modAsset( "balloon_red" ), ShipWright.modAsset( "balloon_black" ) );
		ModelBakery.addVariantName( itemGauge, ShipWright.modAsset( "gauge_basic" ), ShipWright.modAsset( "gauge_advanced" ) );
	}

	@Override
	public void init()
	{
		super.init();

		// Register item models for blocks
		Item itemBalloon   = GameRegistry.findItem( ModInfo.MODID, BlockBalloon.getName()   );
		Item itemBuffer    = GameRegistry.findItem( ModInfo.MODID, BlockBuffer.getName()    );
		Item itemCrateWood = GameRegistry.findItem( ModInfo.MODID, BlockCrateWood.getName() );
		Item itemEngine    = GameRegistry.findItem( ModInfo.MODID, BlockEngine.getName()    );
		Item itemFloater   = GameRegistry.findItem( ModInfo.MODID, BlockFloater.getName()   );
		Item itemGauge     = GameRegistry.findItem( ModInfo.MODID, BlockGauge.getName()     );
		Item itemMarker    = GameRegistry.findItem( ModInfo.MODID, BlockMarker.getName()    );
		Item itemSeat      = GameRegistry.findItem( ModInfo.MODID, BlockSeat.getName()      );

		ModelResourceLocation itemModelResourceLocation;
		final ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		for (int i = 0; i < ItemDye.dyeColors.length; i++)
		{
			modelMesher.register( itemBalloon, i, new ModelResourceLocation( ShipWright.modAsset( BlockBalloon.getName() + "_" + EnumDyeColor.byMetadata( i ).getName() ), "inventory" ) );
		}

		modelMesher.register( itemBuffer,    0, new ModelResourceLocation( ShipWright.modAsset( BlockBuffer.getName()    ), "inventory" ) );
		modelMesher.register( itemCrateWood, 0, new ModelResourceLocation( ShipWright.modAsset( BlockCrateWood.getName() ), "inventory" ) );
		modelMesher.register( itemEngine,    0, new ModelResourceLocation( ShipWright.modAsset( BlockEngine.getName()    ), "inventory" ) );
		modelMesher.register( itemFloater,   0, new ModelResourceLocation( ShipWright.modAsset( BlockFloater.getName()   ), "inventory" ) );
		modelMesher.register( itemGauge,     0, new ModelResourceLocation( ShipWright.modAsset( BlockGauge.getName() + "_basic"    ), "inventory" ) );
		modelMesher.register( itemGauge,     1, new ModelResourceLocation( ShipWright.modAsset( BlockGauge.getName() + "_advanced" ), "inventory" ) );
		modelMesher.register( itemMarker,    0, new ModelResourceLocation( ShipWright.modAsset( BlockMarker.getName()    ), "inventory" ) );
		modelMesher.register( itemSeat,      0, new ModelResourceLocation( ShipWright.modAsset( BlockSeat.getName()      ), "inventory" ) );
}

	@Override
	public void postInit()
	{
		super.postInit();
		// TODO Auto-generated method stub
	}
}
