package io.github.tehstoneman.shipwright.client.gui;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.AssembleResult;
import io.github.tehstoneman.shipwright.inventory.ContainerHelm;
import io.github.tehstoneman.shipwright.network.MsgClientHelmAction;
import io.github.tehstoneman.shipwright.network.MsgClientRenameShip;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;

import java.io.IOException;
import java.util.Locale;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
	public static final ResourceLocation	BACKGROUND_TEXTURE	= new ResourceLocation( ModInfo.MODID, "textures/gui/shipstatus.png" );

	public final TileEntityHelm				tileEntity;
	public final EntityPlayer				player;

	private GuiButton						btnAssemble, btnUndo, btnMount;
	private GuiTextField					txtShipName;
	private boolean							busyCompiling;

	public GuiHelm( TileEntityHelm tileentity, EntityPlayer entityplayer )
	{
		super( new ContainerHelm( tileentity, entityplayer ) );
		tileEntity = tileentity;
		player = entityplayer;

		xSize = 176;
		ySize = 132;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		Keyboard.enableRepeatEvents( true );

		buttonList.clear();

		btnAssemble = new GuiButton( Buttons.COMPILE.getValue(), guiLeft + 7, guiTop + 85, 80, 20, I18n.format( "gui.shipstatus.compile" ) );
		buttonList.add( btnAssemble );

		btnUndo = new GuiButton( Buttons.UNDO.getValue(), guiLeft + 89, guiTop + 85, 80, 20, I18n.format( "gui.shipstatus.undo" ) );
		btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		buttonList.add( btnUndo );

		btnMount = new GuiButton( Buttons.MOUNT.getValue(), guiLeft + 89, guiTop + 105, 80, 20, I18n.format( "gui.shipstatus.mount" ) );
		btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
		buttonList.add( btnMount );

		txtShipName = new GuiTextField( 0, fontRendererObj, guiLeft + 20, guiTop + 6, 120, 10 );
		txtShipName.setMaxStringLength( 127 );
		txtShipName.setEnableBackgroundDrawing( false );
		txtShipName.setVisible( true );
		txtShipName.setCanLoseFocus( false );
		txtShipName.setTextColor( 0xFFFFFF );
		txtShipName.setText( tileEntity.getShipInfo().shipName );
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
		btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTick, int mouseX, int mouseY )
	{
		GL11.glColor4f( 1F, 1F, 1F, 1F );
		mc.renderEngine.bindTexture( BACKGROUND_TEXTURE );
		drawTexturedModalRect( guiLeft, guiTop, 0, 0, xSize, ySize );

		final int xAxis = mouseX - guiLeft;
		final int yAxis = mouseY - guiTop;

		// Edit Ship Name Button
		final boolean mouseOver = xAxis >= 6 && xAxis <= 17 && yAxis >= 4 && yAxis <= 15;
		drawTexturedModalRect( guiLeft + 6, guiTop + 4, 176, mouseOver ? 12 : 0, 12, 12 );

		txtShipName.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
	{
		final int xAxis = mouseX - guiLeft;
		final int yAxis = mouseY - guiTop;

		final AssembleResult result = tileEntity.getAssembleResult();

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
			rcode = busyCompiling ? AssembleResult.RESULT_BUSY_COMPILING : AssembleResult.RESULT_NONE;
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
			if( rcode != AssembleResult.RESULT_NONE )
				busyCompiling = false;
		}

		String rcodename;
		int color1;
		switch( rcode )
		{
		case AssembleResult.RESULT_NONE:
			color1 = color;
			rcodename = "gui.shipstatus.result.none";
			break;
		case AssembleResult.RESULT_OK:
			color1 = 0x40A000;
			rcodename = "gui.shipstatus.result.ok";
			break;
		case AssembleResult.RESULT_OK_WITH_WARNINGS:
			color1 = 0xFFAA00;
			rcodename = "gui.shipstatus.result.okwarn";
			break;
		case AssembleResult.RESULT_MISSING_MARKER:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.missingmarker";
			break;
		case AssembleResult.RESULT_BLOCK_OVERFLOW:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.overflow";
			break;
		case AssembleResult.RESULT_ERROR_OCCURED:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.error";
			break;
		case AssembleResult.RESULT_BUSY_COMPILING:
			color1 = color;
			rcodename = "gui.shipstatus.result.busy";
			break;
		case AssembleResult.RESULT_INCONSISTENT:
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
			{
				buttonAction( Buttons.RENAME );
			}
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
