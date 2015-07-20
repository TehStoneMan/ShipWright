package io.github.tehstoneman.shipwright.items;

import io.github.tehstoneman.shipwright.ModInfo;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ShipItems
{
	public static Item	wheel;

	public static void registerItems()
	{
		wheel = new ItemWheel().setUnlocalizedName( ModInfo.MODID + "." + ItemWheel.getName() );

		GameRegistry.registerItem( wheel, ItemWheel.getName() );
	}

}
