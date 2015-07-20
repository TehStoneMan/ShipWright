package io.github.tehstoneman.shipwright.block;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.item.ItemBalloon;
import io.github.tehstoneman.shipwright.item.ItemCrateWood;
import io.github.tehstoneman.shipwright.item.ItemEngine;
import io.github.tehstoneman.shipwright.item.ItemGauge;
import io.github.tehstoneman.shipwright.item.ItemMarker;
import io.github.tehstoneman.shipwright.item.ItemSeat;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ShipBlocks
{
	public static Block	helm;
	public static Block	balloon;
	public static Block	buffer;
	public static Block	crateWood;
	public static Block	engine;
	public static Block	floater;
	public static Block	gauge;
	public static Block	seat;

	public static void registerBlocks()
	{
		helm      = new      BlockHelm().setUnlocalizedName( ModInfo.MODID + "." +      BlockHelm.getName() );
		balloon   = new   BlockBalloon().setUnlocalizedName( ModInfo.MODID + "." +   BlockBalloon.getName() );
		buffer    = new    BlockBuffer().setUnlocalizedName( ModInfo.MODID + "." +    BlockBuffer.getName() );
		crateWood = new BlockCrateWood().setUnlocalizedName( ModInfo.MODID + "." + BlockCrateWood.getName() );
		engine    = new    BlockEngine().setUnlocalizedName( ModInfo.MODID + "." +    BlockEngine.getName() );
		floater   = new   BlockFloater().setUnlocalizedName( ModInfo.MODID + "." +   BlockFloater.getName() );
		gauge     = new     BlockGauge().setUnlocalizedName( ModInfo.MODID + "." +     BlockGauge.getName() );
		seat      = new      BlockSeat().setUnlocalizedName( ModInfo.MODID + "." +      BlockSeat.getName() );

		GameRegistry.registerBlock( helm,      ItemMarker.class,    BlockHelm.getName() );
		GameRegistry.registerBlock( balloon,   ItemBalloon.class,   BlockBalloon.getName() );
		GameRegistry.registerBlock( buffer,    BlockBuffer.getName()  );
		GameRegistry.registerBlock( crateWood, ItemCrateWood.class, BlockCrateWood.getName() );
		GameRegistry.registerBlock( engine,    ItemEngine.class,    BlockEngine.getName() );
		GameRegistry.registerBlock( floater,   BlockFloater.getName() );
		GameRegistry.registerBlock( gauge,     ItemGauge.class,     BlockGauge.getName() );
		GameRegistry.registerBlock( seat,      ItemSeat.class,      BlockSeat.getName() );
		
		GameRegistry.registerTileEntity( TileEntityHelm.class, "tile_entity_helm" );
	}
}
