package io.github.tehstoneman.shipwright.util;

import io.github.tehstoneman.shipwright.tileentity.TileEntityCrate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonHookContainer
{
	@SubscribeEvent
	public void onInteractWithEntity(EntityInteractEvent event)
	{
		if (event.entityPlayer != null)
		{
			int x = MathHelper.floor_double(event.target.posX);
			int y = MathHelper.floor_double(event.target.posY);
			int z = MathHelper.floor_double(event.target.posZ);
			
			TileEntity te = event.entity.worldObj.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityCrate && ((TileEntityCrate) te).getContainedEntity() == event.target)
			{
				((TileEntityCrate) te).releaseEntity();
				event.setCanceled(true);
			}
		}
	}
}
