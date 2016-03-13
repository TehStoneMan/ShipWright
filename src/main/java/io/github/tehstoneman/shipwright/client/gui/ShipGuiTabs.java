package io.github.tehstoneman.shipwright.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ShipGuiTabs
{
	public static ShipGuiTabs[]		shipGuiTabArray	= new ShipGuiTabs[3];
	public static final ShipGuiTabs	tabInfo			= new ShipGuiTabs( 0, "shipInfoTab" )
													{
														@Override
														@SideOnly( Side.CLIENT )
														public Item getTabIconItem()
														{
															return null;
														}

														@Override
														public void drawTabIcon( Gui gui, int x, int y )
														{
															// TODO Auto-generated method stub
															
														}
													};

	public static final ShipGuiTabs	tabUser			= new ShipGuiTabs( 1, "shipUserTab" )
													{
														@Override
														@SideOnly( Side.CLIENT )
														public Item getTabIconItem()
														{
															return null;
														}

														@Override
														public void drawTabIcon( Gui gui, int x, int y )
														{
															// TODO Auto-generated method stub
															
														}
													};

	public static final ShipGuiTabs	tabBlocks		= new ShipGuiTabs( 2, "shipBlocksTab" )
													{
														@Override
														@SideOnly( Side.CLIENT )
														public Item getTabIconItem()
														{
															return Item.getItemFromBlock( Blocks.planks );
														}

														@Override
														public void drawTabIcon( Gui gui, int x, int y )
														{
															// TODO Auto-generated method stub
															
														}
													};

	private final int				tabIndex;
	private final String			tabLabel;
	private ItemStack				iconItemStack;
	private boolean					drawTitle;
	private boolean					hasScrollbar;

	public ShipGuiTabs( String label )
	{
		this( getNextID(), label );
	}

	public ShipGuiTabs( int index, String label )
	{
		if( index >= shipGuiTabArray.length )
		{
			final ShipGuiTabs[] tmp = new ShipGuiTabs[index + 1];
			for( int x = 0; x < shipGuiTabArray.length; x++ )
				tmp[x] = shipGuiTabArray[x];
			shipGuiTabArray = tmp;
		}
		tabIndex = index;
		tabLabel = label;
		shipGuiTabArray[index] = this;
	}

	@SideOnly( Side.CLIENT )
	public int getTabIndex()
	{
		return tabIndex;
	}

	@SideOnly( Side.CLIENT )
	public String getTabLabel()
	{
		return tabLabel;
	}

	/**
	 * Gets the translated Label.
	 */
	@SideOnly( Side.CLIENT )
	public String getTranslatedTabLabel()
	{
		return "shipGui." + getTabLabel();
	}

	@SideOnly( Side.CLIENT )
	public ItemStack getIconItemStack()
	{
		if( iconItemStack == null && getTabIconItem() != null )
			iconItemStack = new ItemStack( getTabIconItem(), 1, getIconItemDamage() );

		return iconItemStack;
	}

	@SideOnly( Side.CLIENT )
	public abstract Item getTabIconItem();

	@SideOnly( Side.CLIENT )
	public abstract void drawTabIcon( Gui gui, int x, int y );

	@SideOnly( Side.CLIENT )
	public int getIconItemDamage()
	{
		return 0;
	}

	@SideOnly( Side.CLIENT )
	public boolean drawInForegroundOfTab()
	{
		return drawTitle;
	}

	public ShipGuiTabs setNoScrollbar()
	{
		hasScrollbar = false;
		return this;
	}

	/**
	 * returns index % 6
	 */
	@SideOnly( Side.CLIENT )
	public int getTabColumn()
	{
		if( tabIndex > 11 )
			return ( tabIndex - 12 ) % 10 % 5;
		return tabIndex % 6;
	}

	/**
	 * returns tabIndex < 6
	 */
	@SideOnly( Side.CLIENT )
	public boolean isTabInFirstRow()
	{
		if( tabIndex > 11 )
			return ( tabIndex - 12 ) % 10 < 5;
		return tabIndex < 6;
	}

	public int getTabPage()
	{
		if( tabIndex > 11 )
			return ( tabIndex - 12 ) / 10 + 1;
		return 0;
	}

	public static int getNextID()
	{
		return shipGuiTabArray.length;
	}
}