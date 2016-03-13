package io.github.tehstoneman.shipwright.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityEntityAttachment extends Entity implements IEntityAdditionalSpawnData
{
	private EntityShip		ship;
	private BlockPos	pos;
	private Entity			prevRiddenByEntity;
	
	public EntityEntityAttachment(World world)
	{
		super(world);
		ship = null;
		pos = null;
		prevRiddenByEntity = null;
		setSize(0F, 0F);
	}
	
	public void setParentShip(EntityShip entityship, int x, int y, int z)
	{
		ship = entityship;
		if (entityship != null)
		{
			pos = new BlockPos(x, y, z);
			setLocationAndAngles(entityship.posX, entityship.posY, entityship.posZ, 0F, 0F);
		}
	}
	
	public EntityShip getParentShip()
	{
		return ship;
	}
	
	public BlockPos getBlockPos()
	{
		return pos;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (ship != null)
		{
			setPosition(ship.posX, ship.posY, ship.posZ);
		}
		
		if (!worldObj.isRemote)
		{
			if (riddenByEntity == null)
			{
				if (prevRiddenByEntity != null)
				{
					if (ship != null && ship.isFlying())
					{
						EntityParachute parachute = new EntityParachute(worldObj, ship, pos.getX(), pos.getY(), pos.getZ());
						if (worldObj.spawnEntityInWorld(parachute))
						{
							prevRiddenByEntity.mountEntity(parachute);
							prevRiddenByEntity.setSneaking(false);
						}
					}
					prevRiddenByEntity = null;
				}
			}
			
			if (riddenByEntity != null)
			{
				prevRiddenByEntity = riddenByEntity;
			}
		}
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	public void updateRiderPosition()
	{
		if (ship != null)
		{
			ship.updateRiderPosition(riddenByEntity, pos, 0);
		}
	}
	
	@Override
	public double getMountedYOffset()
	{
		return (double)this.height * 0.75D + 0.5d;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return null;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return null;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		setDead();
	}
	
	@Override
	public void writeSpawnData(ByteBuf data)
	{
		if (ship == null)
		{
			data.writeInt(0);
			data.writeByte(0);
			data.writeByte(0);
			data.writeByte(0);
			return;
		}
		data.writeInt(ship.getEntityId());
		data.writeByte(pos.getX() & 0xFF);
		data.writeByte(pos.getY() & 0xFF);
		data.writeByte(pos.getZ() & 0xFF);
	}
	
	@Override
	public void readSpawnData(ByteBuf data)
	{
		Entity entity = worldObj.getEntityByID(data.readInt());
		if (entity instanceof EntityShip)
		{
			setParentShip((EntityShip) entity, data.readUnsignedByte(), data.readUnsignedByte(), data.readUnsignedByte());
		}
	}
}
