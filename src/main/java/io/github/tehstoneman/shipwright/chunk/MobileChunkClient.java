package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.client.render.MobileChunkRenderer;
import io.github.tehstoneman.shipwright.entity.EntityShip;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MobileChunkClient extends MobileChunk
{
	private MobileChunkRenderer	renderer;
	
	public MobileChunkClient(World world, EntityShip entityship)
	{
		super(world, entityship);
		renderer = new MobileChunkRenderer(this);
	}
	
	public MobileChunkRenderer getRenderer()
	{
		return renderer;
	}
	
	@Override
	public void onChunkUnload()
	{
		List<TileEntity> iterator = new ArrayList<TileEntity>(chunkTileEntityMap.values());
		for (TileEntity te : iterator)
		{
			removeChunkBlockTileEntity(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
		}
		super.onChunkUnload();
		renderer.markRemoved();
	}
	
	@Override
	public void setChunkModified()
	{
		super.setChunkModified();
		renderer.markDirty();
	}
}
