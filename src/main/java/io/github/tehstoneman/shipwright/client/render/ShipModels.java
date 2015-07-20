package io.github.tehstoneman.shipwright.client.render;

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
import io.github.tehstoneman.shipwright.items.ItemWheel;
import io.github.tehstoneman.shipwright.items.ShipItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ShipModels
{
	public static void Register()
	{
		// Register item models for blocks
		final Item itemHelm      = GameRegistry.findItem( ModInfo.MODID, BlockHelm.getName() );
		final Item itemBalloon   = GameRegistry.findItem( ModInfo.MODID, BlockBalloon.getName() );
		final Item itemBuffer    = GameRegistry.findItem( ModInfo.MODID, BlockBuffer.getName() );
		final Item itemCrateWood = GameRegistry.findItem( ModInfo.MODID, BlockCrateWood.getName() );
		final Item itemEngine    = GameRegistry.findItem( ModInfo.MODID, BlockEngine.getName() );
		final Item itemFloater   = GameRegistry.findItem( ModInfo.MODID, BlockFloater.getName() );
		final Item itemGauge     = GameRegistry.findItem( ModInfo.MODID, BlockGauge.getName() );
		final Item itemSeat      = GameRegistry.findItem( ModInfo.MODID, BlockSeat.getName() );

		final ModelResourceLocation itemModelResourceLocation;
		final ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		// Items
		modelMesher.register( ShipItems.wheel, 0, new ModelResourceLocation( ShipWright.modAsset( ItemWheel.getName() ), "inventory" ) );

		// Blocks
		modelMesher.register( itemHelm,      0, new ModelResourceLocation( ShipWright.modAsset( BlockHelm.getName() ), "inventory" ) );

		for( int i = 0; i < ItemDye.dyeColors.length; i++ )
			modelMesher.register( itemBalloon, i,
					new ModelResourceLocation( ShipWright.modAsset( BlockBalloon.getName() + "_" + EnumDyeColor.func_176764_b( i ).getName() ),
							"inventory" ) );

		modelMesher.register( itemBuffer,    0, new ModelResourceLocation( ShipWright.modAsset( BlockBuffer.getName() ),              "inventory" ) );
		modelMesher.register( itemCrateWood, 0, new ModelResourceLocation( ShipWright.modAsset( BlockCrateWood.getName() ),           "inventory" ) );
		modelMesher.register( itemEngine,    0, new ModelResourceLocation( ShipWright.modAsset( BlockEngine.getName() ),              "inventory" ) );
		modelMesher.register( itemFloater,   0, new ModelResourceLocation( ShipWright.modAsset( BlockFloater.getName() ),             "inventory" ) );
		modelMesher.register( itemGauge,     0, new ModelResourceLocation( ShipWright.modAsset( BlockGauge.getName() + "_basic" ),    "inventory" ) );
		modelMesher.register( itemGauge,     1, new ModelResourceLocation( ShipWright.modAsset( BlockGauge.getName() + "_advanced" ), "inventory" ) );
		modelMesher.register( itemSeat,      0, new ModelResourceLocation( ShipWright.modAsset( BlockSeat.getName() ),                "inventory" ) );
	}
}
