package io.github.tehstoneman.shipwright.client.gui;

import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.inventory.ContainerEngine;
import io.github.tehstoneman.shipwright.inventory.ContainerHelm;
import io.github.tehstoneman.shipwright.inventory.ContainerShip;
import io.github.tehstoneman.shipwright.tileentity.TileEntityEngine;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ASGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te;
		switch (ID)
		{
		case 1:
			te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityHelm)
			{
				return new ContainerHelm((TileEntityHelm) te, player);
			}
			return null;
		case 2:
			if (player.ridingEntity instanceof EntityShip)
			{
				EntityShip ship = (EntityShip) player.ridingEntity;
				return new ContainerShip(ship, player);
			}
			return null;
		case 3:
			te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityEngine)
			{
				return new ContainerEngine((TileEntityEngine) te, player);
			}
			return null;
		default:
			return null;
		}
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te;
		switch (ID)
		{
		case 1:
			te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityHelm)
			{
				return new GuiHelm((TileEntityHelm) te, player);
			}
			return null;
		case 2:
			if (player.ridingEntity instanceof EntityShip)
			{
				EntityShip ship = (EntityShip) player.ridingEntity;
				return new GuiShip(ship, player);
			}
			return null;
		case 3:
			te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityEngine)
			{
				return new GuiEngine((TileEntityEngine) te, player);
			}
			return null;
		default:
			return null;
		}
	}
}
