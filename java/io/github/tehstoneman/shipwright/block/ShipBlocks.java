package io.github.tehstoneman.shipwright.block;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.item.ItemBalloon;
import io.github.tehstoneman.shipwright.item.ItemCrateWood;
import io.github.tehstoneman.shipwright.item.ItemEngine;
import io.github.tehstoneman.shipwright.item.ItemGauge;
import io.github.tehstoneman.shipwright.item.ItemMarker;
import io.github.tehstoneman.shipwright.item.ItemSeat;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ShipBlocks
{
	public static Block	blockBalloon;
	public static Block	blockBuffer;
	public static Block	blockCrateWood;
	public static Block	blockEngine;
	public static Block	blockFloater;
	public static Block	blockGauge;
	public static Block	blockMarker;
	public static Block	blockSeat;

	public static void registerBlocks()
	{
		blockBalloon   = new   BlockBalloon().setUnlocalizedName( ModInfo.MODID + "." +   BlockBalloon.getName() );
		blockBuffer    = new    BlockBuffer().setUnlocalizedName( ModInfo.MODID + "." +    BlockBuffer.getName() );
		blockCrateWood = new BlockCrateWood().setUnlocalizedName( ModInfo.MODID + "." + BlockCrateWood.getName() );
		blockEngine    = new    BlockEngine().setUnlocalizedName( ModInfo.MODID + "." +    BlockEngine.getName() );
		blockFloater   = new   BlockFloater().setUnlocalizedName( ModInfo.MODID + "." +   BlockFloater.getName() );
		blockGauge     = new     BlockGauge().setUnlocalizedName( ModInfo.MODID + "." +     BlockGauge.getName() );
		blockMarker    = new    BlockMarker().setUnlocalizedName( ModInfo.MODID + "." +    BlockMarker.getName() );
		blockSeat      = new      BlockSeat().setUnlocalizedName( ModInfo.MODID + "." +      BlockSeat.getName() );

		GameRegistry.registerBlock( blockBalloon,   ItemBalloon.class,     BlockBalloon.getName() );
		GameRegistry.registerBlock( blockBuffer,    BlockBuffer.getName()  );
		GameRegistry.registerBlock( blockCrateWood, ItemCrateWood.class, BlockCrateWood.getName() );
		GameRegistry.registerBlock( blockEngine,    ItemEngine.class,       BlockEngine.getName() );
		GameRegistry.registerBlock( blockFloater,   BlockFloater.getName() );
		GameRegistry.registerBlock( blockGauge,     ItemGauge.class,         BlockGauge.getName() );
		GameRegistry.registerBlock( blockMarker,    ItemMarker.class,       BlockMarker.getName() );
		GameRegistry.registerBlock( blockSeat,      ItemSeat.class,           BlockSeat.getName() );
	}
}
