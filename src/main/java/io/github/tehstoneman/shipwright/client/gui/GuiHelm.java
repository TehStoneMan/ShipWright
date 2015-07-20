package io.github.tehstoneman.shipwright.client.gui;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.AssembleResult;
import io.github.tehstoneman.shipwright.inventory.ContainerHelm;
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

public class GuiHelm extends GuiContainer
{
	public static final ResourceLocation	BACKGROUND_TEXTURE	= new ResourceLocation( ModInfo.MODID, "textures/gui/shipstatus.png" );

	public final TileEntityHelm				tileEntity;
	public final EntityPlayer				player;

	private GuiButton						btnAssemble, btnUndo, btnMount;
	private GuiTextField					txtShipName;
	private boolean							busyCompiling;

	private final int						BTN_RENAME			= 1;

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

		final int btnx = guiLeft - 100;
		int btny = guiTop + 20;
		buttonList.clear();

		btnAssemble = new GuiButton( 1, btnx, btny += 20, 100, 20, StatCollector.translateToLocal( "gui.shipstatus.compile" ) );
		buttonList.add( btnAssemble );

		btnUndo = new GuiButton( 2, btnx, btny += 20, 100, 20, StatCollector.translateToLocal( "gui.shipstatus.undo" ) );
		btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		buttonList.add( btnUndo );

		btnMount = new GuiButton( 3, btnx, btny += 20, 100, 20, StatCollector.translateToLocal( "gui.shipstatus.mount" ) );
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

		int y = guiTop + 20;
		btnAssemble.yPosition = y += 20;
		btnUndo.yPosition = y += 20;
		btnMount.yPosition = y += 20;
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

		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.compilerresult" ), col0, row += 10, color );
		fontRendererObj.drawString( StatCollector.translateToLocal( rcodename ), col1, row, color1 );

		final float balloonratio = (float)rballoons / rblocks;
		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.shiptype" ), col0, row += 10, color );
		if( rblocks == 0 )
			fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.type.unknown" ), col1, row, color );
		else
			fontRendererObj.drawString( StatCollector
					.translateToLocal( balloonratio > ShipWright.instance.modConfig.flyBalloonRatio ? "gui.shipstatus.type.airship"
							: "gui.shipstatus.type.boat" ), col1, row, color );

		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.count.block" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rblocks ), col1, row, color );

		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.count.balloon" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rballoons ) + " (" + (int)( balloonratio * 100f ) + "%)", col1, row, color );

		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.count.tileentity" ), col0, row += 10, color );
		fontRendererObj.drawString( String.valueOf( rtes ), col1, row, color );

		fontRendererObj.drawString( StatCollector.translateToLocal( "gui.shipstatus.mass" ), col0, row += 10, color );
		fontRendererObj.drawString( String.format( Locale.ROOT, "%.1f %s", rmass, StatCollector.translateToLocal( "gui.shipstatus.massunit" ) ),
				col1, row, color );

		// Tool tips
		// Edit Ship Name Button
		if( xAxis >= 6 && xAxis <= 17 && yAxis >= 4 && yAxis <= 15 )
			if( txtShipName.isFocused() )
				drawCreativeTabHoveringText( I18n.format( "gui.shipstatus.done", new Object[0] ), xAxis, yAxis );
			else
				drawCreativeTabHoveringText( I18n.format( "gui.shipstatus.rename", new Object[0] ), xAxis, yAxis );
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
				buttonAction( BTN_RENAME );

				final MsgClientRenameShip msg = new MsgClientRenameShip( tileEntity, tileEntity.getShipInfo().shipName );
				ShipWright.instance.pipeline.sendToServer( msg );
			}
		}
	}

	@Override
	public void actionPerformed( GuiButton button )
	{
		//@formatter:off
		/*
		if (button == btnRename)
		{
			if (txtShipName.isFocused())
			{
				btnRename.displayString = StatCollector.translateToLocal("gui.shipstatus.rename");
				tileEntity.getShipInfo().shipName = txtShipName.getText();
				txtShipName.setFocused(false);
				//txtShipName.setEnableBackgroundDrawing(false);

				MsgClientRenameShip msg = new MsgClientRenameShip(tileEntity, tileEntity.getShipInfo().shipName);
				ShipWright.instance.pipeline.sendToServer(msg);
			} else
			{
				btnRename.displayString = StatCollector.translateToLocal("gui.shipstatus.done");
				txtShipName.setFocused(true);
				//txtShipName.setEnableBackgroundDrawing(true);
			}
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

	public void buttonAction( int button )
	{
		final SoundHandler soundHandler = mc.getSoundHandler();
		soundHandler.playSound( PositionedSoundRecord.create( new ResourceLocation( "gui.button.press" ), 1.0F ) );

		switch( button )
		{
		case BTN_RENAME:
			if( txtShipName.isFocused() )
			{
				tileEntity.getShipInfo().shipName = txtShipName.getText();
				txtShipName.setFocused( false );

				final MsgClientRenameShip msg = new MsgClientRenameShip( tileEntity, tileEntity.getShipInfo().shipName );
				ShipWright.instance.pipeline.sendToServer( msg );
			}
			else
				txtShipName.setFocused( true );
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
				buttonAction( BTN_RENAME );
			else
				if( txtShipName.textboxKeyTyped( c, k ) )
				{}
				else
					super.keyTyped( c, k );
	}
}
