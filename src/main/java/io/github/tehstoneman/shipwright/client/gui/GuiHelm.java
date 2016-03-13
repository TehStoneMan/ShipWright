package io.github.tehstoneman.shipwright.client.gui;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.AssembleResultOld;
import io.github.tehstoneman.shipwright.inventory.ContainerHelm;
import io.github.tehstoneman.shipwright.network.MsgClientHelmAction;
import io.github.tehstoneman.shipwright.network.MsgClientRenameShip;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

enum Buttons
{
	RENAME( 0 ), COMPILE( 1 ), UNDO( 2 ), MOUNT( 3 );

	private static final Buttons[]	index	= new Buttons[values().length];
	private final int				value;

	private Buttons( int i )
	{
		value = i;
	}

	public int getValue()
	{
		return value;
	}

	public static Buttons getButton( int i )
	{
		if( i < 0 || i >= index.length )
			i = 0;

		return index[i];
	}
}

public class GuiHelm extends GuiContainer
{
	private static final ResourceLocation	helmGuiBackground		= new ResourceLocation( ModInfo.MODID, "textures/gui/shipstatus.png" );
	private static final ResourceLocation	creativeInventoryTabs	= new ResourceLocation( "textures/gui/container/creative_inventory/tabs.png" );

	public final TileEntityHelm				tileEntity;
	public final EntityPlayer				player;

	private GuiButton						btnAssemble;
	// private GuiButton btnUndo, btnMount;
	private GuiTextField					txtShipName;
	private boolean							busyCompiling;
	//private static int						selectedTabIndex		= ShipGuiTabs.tabInfo.getTabIndex();

	public GuiHelm( TileEntityHelm tileentity, EntityPlayer entityplayer )
	{
		super( new ContainerHelm( tileentity, entityplayer ) );
		tileEntity = tileentity;
		player = entityplayer;

		xSize = 176;
		ySize = 150;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.clear();
		Keyboard.enableRepeatEvents( true );

		txtShipName = new GuiTextField( 0, fontRendererObj, guiLeft + 20, guiTop + 6, 120, fontRendererObj.FONT_HEIGHT );
		txtShipName.setMaxStringLength( 127 );
		txtShipName.setEnableBackgroundDrawing( false );
		txtShipName.setVisible( true );
		txtShipName.setCanLoseFocus( false );
		txtShipName.setTextColor( 0xFFFFFF );
		txtShipName.setText( tileEntity.getShipInfo().shipName );

		//final int i = selectedTabIndex;
		//selectedTabIndex = -1;

		//setCurrentGuiTab( ShipGuiTabs.shipGuiTabArray[i] );

		String btnText = "gui.shipstatus.compile";
		if( tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResultOld.RESULT_OK )
			btnText = "gui.shipstatus.refresh";
		btnAssemble = new GuiButton( Buttons.COMPILE.getValue(), guiLeft + 7, guiTop + 123, 80, 20, I18n.format( btnText ) );
		buttonList.add( btnAssemble );

		/*
		 * btnUndo = new GuiButton( Buttons.UNDO.getValue(), guiLeft + 89, guiTop + 85, 80, 20, I18n.format( "gui.shipstatus.undo" ) );
		 * btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		 * buttonList.add( btnUndo );
		 */

		/*
		 * btnMount = new GuiButton( Buttons.MOUNT.getValue(), guiLeft + 89, guiTop + 105, 80, 20, I18n.format( "gui.shipstatus.mount" ) );
		 * btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
		 * buttonList.add( btnMount );
		 */
	}

	private void setCurrentGuiTab( ShipGuiTabs shipGuiTabs )
	{
		if( shipGuiTabs == null )
			return;
		//final int i = selectedTabIndex;
		//selectedTabIndex = shipGuiTabs.getTabIndex();
		dragSplittingSlots.clear();

		/*
		 * if (shipGuiTabs == CreativeTabs.tabInventory)
		 * {
		 * Container container = this.mc.thePlayer.inventoryContainer;
		 *
		 * if (this.field_147063_B == null)
		 * {
		 * this.field_147063_B = guicontainercreative$containercreative.inventorySlots;
		 * }
		 *
		 * guicontainercreative$containercreative.inventorySlots = Lists.<Slot>newArrayList();
		 *
		 * for (int j = 0; j < container.inventorySlots.size(); ++j)
		 * {
		 * Slot slot = new GuiContainerCreative.CreativeSlot((Slot)container.inventorySlots.get(j), j);
		 * guicontainercreative$containercreative.inventorySlots.add(slot);
		 *
		 * if (j >= 5 && j < 9)
		 * {
		 * int j1 = j - 5;
		 * int k1 = j1 / 2;
		 * int l1 = j1 % 2;
		 * slot.xDisplayPosition = 9 + k1 * 54;
		 * slot.yDisplayPosition = 6 + l1 * 27;
		 * }
		 * else if (j >= 0 && j < 5)
		 * {
		 * slot.yDisplayPosition = -2000;
		 * slot.xDisplayPosition = -2000;
		 * }
		 * else if (j < container.inventorySlots.size())
		 * {
		 * int k = j - 9;
		 * int l = k % 9;
		 * int i1 = k / 9;
		 * slot.xDisplayPosition = 9 + l * 18;
		 *
		 * if (j >= 36)
		 * {
		 * slot.yDisplayPosition = 112;
		 * }
		 * else
		 * {
		 * slot.yDisplayPosition = 54 + i1 * 18;
		 * }
		 * }
		 * }
		 *
		 * this.field_147064_C = new Slot(field_147060_v, 0, 173, 112);
		 * guicontainercreative$containercreative.inventorySlots.add(this.field_147064_C);
		 * }
		 * else if (i == CreativeTabs.tabInventory.getTabIndex())
		 * {
		 * guicontainercreative$containercreative.inventorySlots = this.field_147063_B;
		 * this.field_147063_B = null;
		 * }
		 *
		 * if (this.searchField != null)
		 * {
		 * if (shipGuiTabs.hasSearchBar())
		 * {
		 * this.searchField.setVisible(true);
		 * this.searchField.setCanLoseFocus(false);
		 * this.searchField.setFocused(true);
		 * this.searchField.setText("");
		 * this.searchField.width = shipGuiTabs.getSearchbarWidth();
		 * this.searchField.xPosition = this.guiLeft + (82 + 89 ) - this.searchField.width;
		 * this.updateCreativeSearch();
		 * }
		 * else
		 * {
		 * this.searchField.setVisible(false);
		 * this.searchField.setCanLoseFocus(true);
		 * this.searchField.setFocused(false);
		 * }
		 * }
		 */
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents( false );
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		String btnText = "gui.shipstatus.compile";
		if( tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResultOld.RESULT_OK )
			btnText = "gui.shipstatus.refresh";
		btnAssemble.displayString = I18n.format( btnText );

		// btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		// btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTick, int mouseX, int mouseY )
	{
		GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
		//final ShipGuiTabs guiTabs = ShipGuiTabs.shipGuiTabArray[selectedTabIndex];

		// Draw GUI tabs
		//for( final ShipGuiTabs shipTab : ShipGuiTabs.shipGuiTabArray )
			//if( shipTab != null && shipTab.getTabIndex() != selectedTabIndex )
				//drawTab( shipTab );

		// Draw GUI background
		mc.renderEngine.bindTexture( helmGuiBackground );
		drawTexturedModalRect( guiLeft, guiTop, 0, 0, xSize, ySize );

		final int xAxis = mouseX - guiLeft;
		final int yAxis = mouseY - guiTop;

		// Edit Ship Name Button
		final boolean mouseOver = xAxis >= 6 && xAxis <= 17 && yAxis >= 4 && yAxis <= 15;
		drawTexturedModalRect( guiLeft + 6, guiTop + 4, 176, mouseOver ? 12 : 0, 12, 12 );

		txtShipName.drawTextBox();

		// Draw currently selected tab
		//drawTab( guiTabs );
	}

	protected void drawTab( ShipGuiTabs shipTab )
	{
		//final boolean flag = shipTab.getTabIndex() == selectedTabIndex;
		final boolean flag1 = shipTab.isTabInFirstRow();
		final int i = shipTab.getTabColumn();
		final int u = i * 28;
		int v = 0;
		int x = guiLeft + 28 * i;
		int y = guiTop;
		final int h = 32;

		//if( flag )
			//v += 32;

		if( i == 5 )
			x = guiLeft + xSize - 28;
		else
			if( i > 0 )
				x += i;

		if( flag1 )
			y = y - 28;
		else
		{
			v += 64;
			y = y + ySize - 4;
		}

		mc.renderEngine.bindTexture( creativeInventoryTabs );
		GlStateManager.disableLighting();
		GlStateManager.color( 1F, 1F, 1F ); // Forge: Reset color in case Items change it.
		GlStateManager.enableBlend(); // Forge: Make sure blend is enabled else tabs show a white border.
		this.drawTexturedModalRect( x, y, u, v, 28, h );
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		x = x + 6;
		y = y + 8 + ( flag1 ? 1 : -1 );
		shipTab.drawTabIcon( this, x, y );
		/*
		final ItemStack itemstack = shipTab.getIconItemStack();
		if( itemstack != null )
		{
			GlStateManager.enableLighting();
			GlStateManager.enableRescaleNormal();
			itemRender.renderItemAndEffectIntoGUI( itemstack, x, y );
			itemRender.renderItemOverlays( fontRendererObj, itemstack, x, y );
			GlStateManager.disableLighting();
		}
		else
		{
			mc.renderEngine.bindTexture( helmGuiBackground );
			this.drawTexturedModalRect( x, y, 188, 16 * shipTab.getTabIndex(), 16, 16 );
		}
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
		*/
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
	{
		final int xAxis = mouseX - guiLeft;
		final int yAxis = mouseY - guiTop;

		final AssembleResultOld result = tileEntity.getAssembleResult();

		final int color = 0x404040;
		int row = 6;
		final int col0 = 8;
		final int col1 = col0 + xSize / 2;

		row += 5;

		int rcode;
		int rblocks;
		int rballoons;
		int rtes;
		float rmass;

		if( result == null )
		{
			rcode = busyCompiling ? AssembleResultOld.RESULT_BUSY_COMPILING : AssembleResultOld.RESULT_NONE;
			rblocks = rballoons = rtes = 0;
			rmass = 0f;
		}
		else
		{
			rcode = result.getCode();
			rblocks = result.getBlockCount();
			rballoons = result.getBalloonCount();
			rtes = result.getTileEntityCount();
			rmass = result.getMass();
			if( rcode != AssembleResultOld.RESULT_NONE )
				busyCompiling = false;
		}

		String rcodename;
		int color1;
		switch( rcode )
		{
		case AssembleResultOld.RESULT_NONE:
			color1 = color;
			rcodename = "gui.shipstatus.result.none";
			break;
		case AssembleResultOld.RESULT_OK:
			color1 = 0x40A000;
			rcodename = "gui.shipstatus.result.ok";
			break;
		case AssembleResultOld.RESULT_OK_WITH_WARNINGS:
			color1 = 0xFFAA00;
			rcodename = "gui.shipstatus.result.okwarn";
			break;
		case AssembleResultOld.RESULT_MISSING_MARKER:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.missingmarker";
			break;
		case AssembleResultOld.RESULT_BLOCK_OVERFLOW:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.overflow";
			break;
		case AssembleResultOld.RESULT_ERROR_OCCURED:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.error";
			break;
		case AssembleResultOld.RESULT_BUSY_COMPILING:
			color1 = color;
			rcodename = "gui.shipstatus.result.busy";
			break;
		case AssembleResultOld.RESULT_INCONSISTENT:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.inconsistent";
			break;
		default:
			color1 = color;
			rcodename = "gui.shipstatus.result.none";
			break;
		}

		fontRendererObj.drawString( I18n.format( "gui.shipstatus.compilerresult" ), col0, row += 10, color );
		fontRendererObj.drawString( I18n.format( rcodename ), col1, row, color1 );

		final float balloonratio = (float)rballoons / rblocks;
		fontRendererObj.drawString( I18n.format( "gui.shipstatus.shiptype" ), col0, row += 10, color );
		if( rblocks == 0 )
			fontRendererObj.drawString( I18n.format( "gui.shipstatus.type.unknown" ), col1, row, color );
		else
			fontRendererObj.drawString( StatCollector
					.translateToLocal( balloonratio > ShipWright.instance.modConfig.flyBalloonRatio ? "gui.shipstatus.type.airship"
							: "gui.shipstatus.type.boat" ), col1, row, color );

		fontRendererObj.drawString( I18n.format( "gui.shipstatus.count.block" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rblocks ), col1, row, color );

		fontRendererObj.drawString( I18n.format( "gui.shipstatus.count.balloon" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rballoons ) + " (" + (int)( balloonratio * 100f ) + "%)", col1, row, color );

		fontRendererObj.drawString( I18n.format( "gui.shipstatus.count.tileentity" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rtes ), col1, row, color );

		fontRendererObj.drawString( I18n.format( "gui.shipstatus.mass" ), col0, row += 10, color );
		fontRendererObj.drawString( String.format( Locale.ROOT, "%.1f %s", rmass, I18n.format( "gui.shipstatus.massunit" ) ), col1, row, color );

		// Tool tips
		// Edit Ship Name Button
		if( xAxis >= 6 && xAxis <= 17 && yAxis >= 4 && yAxis <= 15 )
			if( txtShipName.isFocused() )
				drawCreativeTabHoveringText( I18n.format( "gui.shipstatus.done" ), xAxis, yAxis );
			else
				drawCreativeTabHoveringText( I18n.format( "gui.shipstatus.rename" ), xAxis, yAxis );
	}

	@Override
	protected void mouseClicked( int mouseX, int mouseY, int button ) throws IOException
	{
		super.mouseClicked( mouseX, mouseY, button );

		if( button == 0 )
		{
			final int xAxis = mouseX - guiLeft;
			final int yAxis = mouseY - guiTop;

			// Edit Ship Name
			if( xAxis >= 6 && xAxis <= 17 && yAxis >= 4 && yAxis <= 15 )
				buttonAction( Buttons.RENAME );
		}
	}

	@Override
	public void actionPerformed( GuiButton button )
	{
		if( button == btnAssemble )
			buttonAction( Buttons.COMPILE );
	}

	public void buttonAction( Buttons button )
	{
		switch( button )
		{
		case RENAME:
			final SoundHandler soundHandler = mc.getSoundHandler();
			soundHandler.playSound( PositionedSoundRecord.create( new ResourceLocation( "gui.button.press" ), 1.0F ) );

			if( txtShipName.isFocused() )
			{
				tileEntity.getShipInfo().shipName = txtShipName.getText();
				txtShipName.setFocused( false );

				ShipWright.network.sendToServer( new MsgClientRenameShip( tileEntity.getShipInfo().shipName, tileEntity.getPos() ) );
			}
			else
				txtShipName.setFocused( true );
			break;
		case COMPILE:
			ShipWright.network.sendToServer( new MsgClientHelmAction( MsgClientHelmAction.BUILD, tileEntity.getPos() ) );
			tileEntity.setAssembleResult( null );
			busyCompiling = true;
			break;
		default:
			break;
		}
		//@formatter:off
		/*
		} else if (button == btnAssemble)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 0);
			ShipWright.instance.pipeline.sendToServer(msg);
			tileEntity.setAssembleResult(null);
			busyCompiling = true;
		} else if (button == btnUndo)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 2);
			ShipWright.instance.pipeline.sendToServer(msg);
		} else if (button == btnMount)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 1);
			ShipWright.instance.pipeline.sendToServer(msg);
		}
		 */
		//@formatter:on
	}

	@Override
	protected void keyTyped( char c, int k ) throws IOException
	{
		if( !checkHotbarKeys( k ) )
			if( k == Keyboard.KEY_RETURN && txtShipName.isFocused() )
				buttonAction( Buttons.RENAME );
			else
				if( txtShipName.textboxKeyTyped( c, k ) )
				{}
				else
					super.keyTyped( c, k );
	}
}
