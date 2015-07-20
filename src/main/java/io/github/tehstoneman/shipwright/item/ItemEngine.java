package io.github.tehstoneman.shipwright.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemEngine extends ItemBlock
{

	public ItemEngine( Block block )
	{
		super( block );
		setMaxDamage( 0 );
	}
}
