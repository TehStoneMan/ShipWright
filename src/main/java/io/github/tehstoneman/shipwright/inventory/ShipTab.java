package io.github.tehstoneman.shipwright.inventory;

import io.github.tehstoneman.shipwright.items.ShipItems;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ShipTab extends CreativeTabs
{

	public ShipTab( String label )
	{
		super( label );
	}

	@Override
	public Item getTabIconItem()
	{
		return ShipItems.wheel;
	}

	@Override
	public void displayAllReleventItems( List itemsToShowOnTab )
	{
	    for (Object itemObject : Item.itemRegistry) {
	        Item item = (Item)itemObject;
	        if (item != null) {
	          if (item.getUnlocalizedName().contains(".shipwright")) {
	            item.getSubItems(item, this, itemsToShowOnTab);  // add all sub items to the list
	          }
	        }
	      }
	}
}
