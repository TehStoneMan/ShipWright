package io.github.tehstoneman.shipwright.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemMarker extends ItemBlock
{

	public ItemMarker( Block block )
	{
		super( block );
		setMaxDamage( 0 );
	}
}
