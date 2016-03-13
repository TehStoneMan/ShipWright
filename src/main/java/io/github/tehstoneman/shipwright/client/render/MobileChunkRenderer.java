package io.github.tehstoneman.shipwright.client.render;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.chunk.MobileChunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

@SideOnly( Side.CLIENT )
public class MobileChunkRenderer
{
	private final MobileChunk			chunk;

	private int							glRenderList		= 0;

	public boolean						isInFrustum			= false;

	/** Should this renderer skip this render pass */
	public boolean[]					skipRenderPass		= new boolean[2];

	/** Boolean for whether this renderer needs to be updated or not */
	public boolean						needsUpdate;
	public boolean						isRemoved;

	/** Axis aligned bounding box */
	public AxisAlignedBB				rendererBoundingBox;

	private boolean						isInitialized		= false;

	/** All the tile entities that have special rendering code for this chunk */
	private final List< TileEntity >	tileEntityRenderers	= new ArrayList< TileEntity >();
	public List< TileEntity >			tileEntities;

	/** Bytes sent to the GPU */
	private int							bytesDrawn;

	public MobileChunkRenderer( MobileChunk mobilechunk )
	{
		chunk = mobilechunk;
		needsUpdate = true;

		tileEntities = new ArrayList< TileEntity >();
	}

	private void tryEndDrawing()
	{
		try
		{
			Tessellator.getInstance().draw();
			LogManager.getLogger( ModInfo.MODID ).trace( "Drawing stopped" );
		}
		catch( final IllegalStateException ise )
		{
			LogManager.getLogger( ModInfo.MODID ).trace( "Not drawing" );
		}
	}

	public void render( float partialticks )
	{
		if( isRemoved )
		{
			if( glRenderList != 0 )
			{
				GLAllocation.deleteDisplayLists( glRenderList );
				glRenderList = 0;
			}
			return;
		}

		if( needsUpdate )
			try
		{
				updateRender();
		}
			catch( final Exception e )
		{
			LogManager.getLogger( ModInfo.MODID ).error( "A mobile chunk render error has occured", e );
			tryEndDrawing();
		}

		if( glRenderList != 0 )
			for( int pass = 0; pass < 2; ++pass )
			{
				GL11.glCallList( glRenderList + pass );

				RenderHelper.enableStandardItemLighting();
				final Iterator< TileEntity > it = tileEntityRenderers.iterator();
				while( it.hasNext() )
				{
					final TileEntity tile = it.next();
					try
					{
						if( tile.shouldRenderInPass( pass ) )
							renderTileEntity( tile, partialticks );
					}
					catch( final Exception e )
					{
						it.remove();
						LogManager.getLogger( ModInfo.MODID ).error( "A tile entity render error has occured", e );
						tryEndDrawing();
					}
				}
			}
	}

	/**
	 * Render this TileEntity at its current position from the player
	 */
	public void renderTileEntity( TileEntity tileentity, float partialticks )
	{
		final int i = chunk.getCombinedLight( tileentity.getPos(), 0 );
		final int j = i % 65536;
		final int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords( OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F );
		GL11.glColor4f( 1F, 1F, 1F, 1F );
		TileEntityRendererDispatcher.instance.renderTileEntityAt( tileentity, tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity
				.getPos().getZ(), partialticks );
	}

	private void updateRender()
	{
		if( glRenderList == 0 )
			glRenderList = GLAllocation.generateDisplayLists( 2 );

		for( int i = 0; i < 2; ++i )
			skipRenderPass[i] = true;

		// chunk.setLightPopulated( false );
		final HashSet< TileEntity > hashset0 = new HashSet< TileEntity >();
		hashset0.addAll( tileEntityRenderers );
		tileEntityRenderers.clear();

		// RenderBlocks renderblocks = new RenderBlocks(chunk);
		bytesDrawn = 0;

		for( int pass = 0; pass < 2; ++pass )
		{
			final boolean flag = false;
			boolean flag1 = false;
			boolean glliststarted = false;

			for( int y = chunk.minY(); y < chunk.maxY(); ++y )
				for( int z = chunk.minZ(); z < chunk.maxZ(); ++z )
					for( int x = chunk.minX(); x < chunk.maxX(); ++x )
					{
						final IBlockState blockState = chunk.getBlockState( new BlockPos( x, y, z ) );
						final Block block = blockState.getBlock();
						if( block != null && block.getMaterial() != Material.air )
						{
							if( !glliststarted )
							{
								glliststarted = true;
								GL11.glNewList( glRenderList + pass, GL11.GL_COMPILE );
								GL11.glPushMatrix();
								final float f = 1.000001F;
								GL11.glTranslatef( -8.0F, -8.0F, -8.0F );
								GL11.glScalef( f, f, f );
								GL11.glTranslatef( 8.0F, 8.0F, 8.0F );
								// Tessellator.getInstance().startDrawingQuads();
							}

							if( pass == 0 && block.hasTileEntity( blockState ) )
							{
								final TileEntity tileentity = chunk.getTileEntity( new BlockPos( x, y, z ) );

								//if( TileEntityRendererDispatcher.instance.hasSpecialRenderer( tileentity ) ) tileEntityRenderers.add( tileentity );
							}

							//@formatter:off
							/*
							int blockpass = block.getRenderBlockPass();
							if (blockpass > pass)
							{
								flag = true;
							}
							if (!block.canRenderInPass(pass))
							{
								continue;
							}
							flag1 |= renderblocks.renderBlockByRenderType(block, x, y, z);
							 */
							//@formatter:on
						}
					}

			if( glliststarted )
			{
				//bytesDrawn += Tessellator.getInstance().draw();
				GL11.glPopMatrix();
				GL11.glEndList();
				// Tessellator.getInstance().setTranslation(0D, 0D, 0D);
			}
			else
				flag1 = false;

			if( flag1 )
				skipRenderPass[pass] = false;

			if( !flag )
				break;
		}

		final HashSet< TileEntity > hashset1 = new HashSet< TileEntity >();
		hashset1.addAll( tileEntityRenderers );
		hashset1.removeAll( hashset0 );
		tileEntities.addAll( hashset1 );
		hashset0.removeAll( tileEntityRenderers );
		tileEntities.removeAll( hashset0 );
		isInitialized = true;

		needsUpdate = false;
	}

	public void markDirty()
	{
		needsUpdate = true;
	}

	public void markRemoved()
	{
		isRemoved = true;

		try
		{
			if( glRenderList != 0 )
			{
				LogManager.getLogger( ModInfo.MODID ).debug( "Deleting mobile chunk display list " + glRenderList );
				GLAllocation.deleteDisplayLists( glRenderList );
				glRenderList = 0;
			}
		}
		catch( final Exception e )
		{
			LogManager.getLogger( ModInfo.MODID ).error( "Failed to destroy mobile chunk display list", e );
		}
	}
}
