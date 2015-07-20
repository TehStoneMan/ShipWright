package io.github.tehstoneman.shipwright.entity;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.block.ShipBlocks;
import io.github.tehstoneman.shipwright.tileentity.TileEntityEngine;
import io.github.tehstoneman.shipwright.util.MaterialDensity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public final class ShipCapabilities
{
	private final EntityShip				ship;
	public float							speedMultiplier, rotationMultiplier, liftMultiplier;
	public float							brakeMult;
	private int								balloons;
	private int								floaters;
	private int								blockCount;
	private float							mass;
	private List<EntityEntityAttachment>	seats;
	private List<TileEntityEngine>			engines;
	private float							enginePower;
	
	//private FuelInventory					fuelInventory;
	
	ShipCapabilities(EntityShip entityship)
	{
		ship = entityship;
		clear();
	}
	
	public float getPoweredSpeedMult()
	{
		return speedMultiplier + enginePower * 0.5f;
	}
	
	public float getPoweredRotationMult()
	{
		return rotationMultiplier + enginePower * 0.25f;
	}
	
	public float getPoweredLiftMult()
	{
		return liftMultiplier + enginePower * 0.5f;
	}
	
	public float getEnginePower()
	{
		return enginePower;
	}
	
	public void updateEngines()
	{
		enginePower = 0f;
		if (engines != null)
		{
			for (TileEntityEngine te : engines)
			{
				te.updateRunning();
				if (te.isRunning())
				{
					enginePower += te.enginePower;
				}
			}
		}
	}
	
	/*public FuelInventory getFuelInventory()
	{
		if (fuelInventory == null)
		{
			fuelInventory = new FuelInventory(ship);
		}
		return fuelInventory;
	}*/
	
	public boolean canFly()
	{
		return ShipWright.instance.modConfig.enableAirShips && balloons >= blockCount * ShipWright.instance.modConfig.flyBalloonRatio;
	}
	
	public int getBlockCount()
	{
		return blockCount;
	}
	
	public int getBalloonCount()
	{
		return balloons;
	}
	
	public int getFloaterCount()
	{
		return floaters;
	}
	
	public float getMass()
	{
		return mass;
	}
	
	public void addAttachments(EntityEntityAttachment entity)
	{
		if (seats == null)
		{
			seats = new ArrayList<EntityEntityAttachment>(4);
		}
		seats.add(entity);
	}
	
	public List<EntityEntityAttachment> getAttachments()
	{
		return seats;
	}
	
	public List<TileEntityEngine> getEngines()
	{
		return engines;
	}
	
	protected void onChunkBlockAdded(Block block, IBlockState iBlockState, int x, int y, int z)
	{
		blockCount++;
		mass += MaterialDensity.getDensity(block);
		if (block == ShipBlocks.balloon)
		{
			balloons++;
		} else if (block == ShipBlocks.floater)
		{
			floaters++;
		} else if (block == ShipBlocks.engine)
		{
			TileEntity te = ship.getShipChunk().getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityEngine)
			{
				if (engines == null)
				{
					engines = new ArrayList<TileEntityEngine>(4);
				}
				engines.add((TileEntityEngine) te);
			}
		} else if (block == ShipBlocks.seat && !ship.worldObj.isRemote)
		{
			int x1 = ship.seatX, y1 = ship.seatY, z1 = ship.seatZ;
			if (ship.frontDirection == 0)
			{
				z1 -= 1;
			} else if (ship.frontDirection == 1)
			{
				x1 += 1;
			} else if (ship.frontDirection == 2)
			{
				z1 += 1;
			} else if (ship.frontDirection == 3)
			{
				x1 -= 1;
			}
			if (x != x1 || y != y1 || z != z1)
			{
				EntitySeat seat = new EntitySeat(ship.worldObj);
				seat.setParentShip(ship, x, y, z);
				addAttachments(seat);
			}
		}
	}
	
	public boolean mountEntity(Entity entity)
	{
		if (seats == null)
		{
			return false;
		}
		
		for (EntityEntityAttachment seat : seats)
		{
			if (seat.riddenByEntity == null)
			{
				entity.mountEntity(seat);
				return true;
			} else if (seat.riddenByEntity == entity)
			{
				seat.mountEntity(null);
				return true;
			}
		}
		return false;
	}
	
	public void spawnSeatEntities()
	{
		if (seats != null)
		{
			for (EntityEntityAttachment seat : seats)
			{
				ship.worldObj.spawnEntityInWorld(seat);
			}
		}
	}
	
	public void clearBlockCount()
	{
		speedMultiplier = rotationMultiplier = liftMultiplier = 1F;
		brakeMult = 0.9F;
		balloons = 0;
		floaters = 0;
		blockCount = 0;
		mass = 0F;
		if (engines != null)
		{
			engines.clear();
			engines = null;
		}
	}
	
	public void clear()
	{
		if (seats != null)
		{
			for (EntityEntityAttachment seat : seats)
			{
				seat.setDead();
			}
			seats = null;
		}
		if (engines != null)
		{
			engines.clear();
			engines = null;
		}
		clearBlockCount();
	}
}
