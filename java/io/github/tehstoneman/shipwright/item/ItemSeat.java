package io.github.tehstoneman.shipwright.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemSeat extends ItemBlock
{

	public ItemSeat( Block block )
	{
		super( block );
		setMaxDamage( 0 );
	}
}
