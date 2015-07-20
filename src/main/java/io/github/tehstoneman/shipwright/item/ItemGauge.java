package io.github.tehstoneman.shipwright.item;

import io.github.tehstoneman.shipwright.block.BlockGauge;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemGauge extends ItemBlock
{

	public ItemGauge( Block block )
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
		BlockGauge.EnumType type = BlockGauge.EnumType.byMetadata( stack.getMetadata() );
		return super.getUnlocalizedName() + "." + type.toString();
	}
}
