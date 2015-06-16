package io.github.tehstoneman.shipwright.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBalloon extends ItemBlock
{
	public ItemBalloon( Block block )
	{
		super( block );
		setMaxDamage( 0 );
		setHasSubtypes( true );
	}

	@Override
	public int getMetadata( int metadata )
	{
		return metadata;
	}

	@Override
	public String getUnlocalizedName( ItemStack stack )
	{
		final EnumDyeColor color = EnumDyeColor.byMetadata( stack.getMetadata() );
		return super.getUnlocalizedName() + "." + color.toString();
	}
}
