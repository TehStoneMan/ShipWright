package io.github.tehstoneman.shipwright.entity;

import io.github.tehstoneman.shipwright.ModInfo;
import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.chunk.AssembleResult;
import io.github.tehstoneman.shipwright.chunk.ChunkDisassembler;
import io.github.tehstoneman.shipwright.chunk.ChunkIO;
import io.github.tehstoneman.shipwright.chunk.MobileChunk;
import io.github.tehstoneman.shipwright.chunk.MobileChunkClient;
import io.github.tehstoneman.shipwright.chunk.MobileChunkServer;
import io.github.tehstoneman.shipwright.chunk.ShipSizeOverflowException;
import io.github.tehstoneman.shipwright.control.ShipControllerClient;
import io.github.tehstoneman.shipwright.control.ShipControllerCommon;
import io.github.tehstoneman.shipwright.tileentity.TileEntityEngine;
import io.github.tehstoneman.shipwright.tileentity.TileEntityHelm;
import io.github.tehstoneman.shipwright.util.AABBRotator;
import io.github.tehstoneman.shipwright.util.MaterialDensity;
import io.github.tehstoneman.shipwright.util.MathHelperMod;
import io.github.tehstoneman.shipwright.util.ModSettings;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;

public class EntityShip extends EntityBoat implements IEntityAdditionalSpawnData
{
	public static final float	BASE_FORWARD_SPEED	= 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;

	public static boolean isAABBInLiquidNotFall( World world, AxisAlignedBB aabb )
	{
		final int i = MathHelper.floor_double( aabb.minX );
		final int j = MathHelper.floor_double( aabb.maxX + 1D );
		final int k = MathHelper.floor_double( aabb.minY );
		final int l = MathHelper.floor_double( aabb.maxY + 1D );
		final int i1 = MathHelper.floor_double( aabb.minZ );
		final int j1 = MathHelper.floor_double( aabb.maxZ + 1D );

		for( int x = i; x < j; ++x )
			for( int y = k; y < l; ++y )
				for( int z = i1; z < j1; ++z )
				{
					final IBlockState blockState = world.getBlockState( new BlockPos( x, y, z ) );
					final Block block = blockState.getBlock();

					if( block != null && ( block.getMaterial() == Material.water || block.getMaterial() == Material.lava ) )
					{
						final int j2 = block.getMetaFromState( blockState );
						double d0 = y + 1;

						if( j2 < 8 )
						{
							d0 = y + 1 - j2 / 8.0D;

							if( d0 >= aabb.minY )
								return true;
						}
					}
				}

		return false;
	}

	private MobileChunk				shipChunk;
	private final ShipCapabilities	capabilities;
	private ShipControllerCommon	controller;
	private ShipHandlerCommon		handler;
	private ShipInfo				info;

	private ChunkDisassembler		disassembler;

	public float					motionYaw;

	public int						frontDirection;
	public int						seatX, seatY, seatZ;
	private Entity					prevRiddenByEntity;

	boolean							isFlying;
	protected float					groundFriction, horFriction, vertFriction;

	int[]							layeredBlockVolumeCount;

	// START ENTITYBOAT MOD VARS
	private boolean					boatIsEmpty;
	private boolean					syncPosWithServer;
	@SideOnly( Side.CLIENT )
	private int						boatPosRotationIncrements;
	@SideOnly( Side.CLIENT )
	private double					boatX, boatY, boatZ;
	@SideOnly( Side.CLIENT )
	private double					boatPitch, boatYaw;
	@SideOnly( Side.CLIENT )
	private double					boatVelX, boalVelY, boatVelZ;

	// END ENTITYBOAT MOD VARS

	public EntityShip( World world )
	{
		super( world );
		info = new ShipInfo();
		capabilities = new ShipCapabilities( this );
		if( world.isRemote )
			initClient();
		else
			initCommon();

		motionYaw = 0F;

		layeredBlockVolumeCount = null;
		frontDirection = 0;
		// yOffset = 0F;

		groundFriction = 0.9F;
		horFriction = 0.994F;
		vertFriction = 0.95F;

		prevRiddenByEntity = null;

		isFlying = false;
		boatIsEmpty = false;
		syncPosWithServer = true;
		if( world.isRemote )
		{
			boatPosRotationIncrements = 0;
			boatX = boatY = boatZ = 0D;
			boatPitch = boatYaw = 0D;
			boatVelX = boalVelY = boatVelZ = 0D;
		}
	}

	@SideOnly( Side.CLIENT )
	private void initClient()
	{
		shipChunk = new MobileChunkClient( worldObj, this );
		handler = new ShipHandlerClient( this );
		controller = new ShipControllerClient();
	}

	private void initCommon()
	{
		shipChunk = new MobileChunkServer( worldObj, this );
		handler = new ShipHandlerServer( this );
		controller = new ShipControllerCommon();
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject( 30, Byte.valueOf( (byte)0 ) );
	}

	public MobileChunk getShipChunk()
	{
		return shipChunk;
	}

	public ShipCapabilities getCapabilities()
	{
		return capabilities;
	}

	public ShipControllerCommon getController()
	{
		return controller;
	}

	public ChunkDisassembler getDisassembler()
	{
		if( disassembler == null )
			disassembler = new ChunkDisassembler( this );
		return disassembler;
	}

	public void setInfo( ShipInfo shipinfo )
	{
		if( shipinfo == null )
			throw new NullPointerException( "Cannot set null ship info" );
		info = shipinfo;
	}

	public ShipInfo getInfo()
	{
		return info;
	}

	public void setPilotSeat( int dir, int seatx, int seaty, int seatz )
	{
		frontDirection = dir;
		seatX = seatx;
		seatY = seaty;
		seatZ = seatz;
	}

	@Override
	public void setDead()
	{
		super.setDead();
		shipChunk.onChunkUnload();
		capabilities.clear();
	}

	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
		if( shipChunk.isModified )
		{
			shipChunk.isModified = false;
			handler.onChunkUpdate();
		}
	}

	public void setRotatedBoundingBox()
	{
		if( shipChunk == null )
		{
			final float hw = width / 2F;
			setEntityBoundingBox( new AxisAlignedBB( posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw ) );
			// boundingBox.setBounds(posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw);
		}
		else
		{
			setEntityBoundingBox( new AxisAlignedBB( posX - shipChunk.getCenterX(), posY, posZ - shipChunk.getCenterZ(), posX
					+ shipChunk.getCenterX(), posY + height, posZ + shipChunk.getCenterZ() ) );
			// boundingBox.setBounds(posX - shipChunk.getCenterX(), posY, posZ - shipChunk.getCenterZ(), posX + shipChunk.getCenterX(), posY + height, posZ +
			// shipChunk.getCenterZ());
			AABBRotator.rotateAABBAroundY( getBoundingBox(), posX, posZ, (float)Math.toRadians( rotationYaw ) );
		}
	}

	@Override
	public void setSize( float w, float h )
	{
		if( w != width || h != height )
		{
			width = w;
			height = h;
			final float hw = w / 2F;
			setEntityBoundingBox( new AxisAlignedBB( posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw ) );
			// boundingBox.setBounds(posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw);
		}

		//@formatter:off
		/*
		float f = w % 2.0F;
		if (f < 0.375D)
		{
			myEntitySize = EnumEntitySize.SIZE_1;
		} else if (f < 0.75D)
		{
			myEntitySize = EnumEntitySize.SIZE_2;
		} else if (f < 1.0D)
		{
			myEntitySize = EnumEntitySize.SIZE_3;
		} else if (f < 1.375D)
		{
			myEntitySize = EnumEntitySize.SIZE_4;
		} else if (f < 1.75D)
		{
			myEntitySize = EnumEntitySize.SIZE_5;
		} else
		{
			myEntitySize = EnumEntitySize.SIZE_6;
		}
		 */
		//@formatter:on
	}

	//@formatter:off
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int incr)
	{
		if (boatIsEmpty)
		{
			boatPosRotationIncrements = incr + 5;
		} else
		{
			double dx = x - posX;
			double dy = y - posY;
			double dz = z - posZ;
			double d = dx * dx + dy * dy + dz * dz;

			if (d < 0.3D)
			{
				return;
			}

			syncPosWithServer = true;
			boatPosRotationIncrements = incr;
		}

		boatX = x;
		boatY = y;
		boatZ = z;
		boatYaw = yaw;
		boatPitch = pitch;
		motionX = boatVelX;
		motionY = boalVelY;
		motionZ = boatVelZ;
	}
	 */
	//@formatter:on

	@Override
	@SideOnly( Side.CLIENT )
	public void setVelocity( double x, double y, double z )
	{
		boatVelX = motionX = x;
		boalVelY = motionY = y;
		boatVelZ = motionZ = z;
	}

	@Override
	public void onUpdate()
	{
		onEntityUpdate();
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		final double horvel = Math.sqrt( motionX * motionX + motionZ * motionZ );
		if( worldObj.isRemote )
		{
			if( riddenByEntity == null )
				setIsBoatEmpty( true );
			spawnParticles( horvel );
		}

		if( worldObj.isRemote && ( boatIsEmpty || syncPosWithServer ) )
		{
			handleClientUpdate();
			if( boatPosRotationIncrements == 0 )
				syncPosWithServer = false;
		}
		else
			handleServerUpdate( horvel );
	}

	@SideOnly( Side.CLIENT )
	protected void handleClientUpdate()
	{
		if( boatPosRotationIncrements > 0 )
		{
			final double dx = posX + ( boatX - posX ) / boatPosRotationIncrements;
			final double dy = posY + ( boatY - posY ) / boatPosRotationIncrements;
			final double dz = posZ + ( boatZ - posZ ) / boatPosRotationIncrements;
			final double ang = MathHelper.wrapAngleTo180_double( boatYaw - rotationYaw );
			rotationYaw = (float)( rotationYaw + ang / boatPosRotationIncrements );
			rotationPitch = (float)( rotationPitch + ( boatPitch - rotationPitch ) / boatPosRotationIncrements );
			boatPosRotationIncrements--;
			setPosition( dx, dy, dz );
			setRotation( rotationYaw, rotationPitch );
		}
		else
		{
			setPosition( posX + motionX, posY + motionY, posZ + motionZ );

			if( onGround )
			{
				motionX *= groundFriction;
				motionY *= groundFriction;
				motionZ *= groundFriction;
			}

			motionX *= horFriction;
			motionY *= vertFriction;
			motionZ *= horFriction;
		}
		setRotatedBoundingBox();
	}

	protected void handleServerUpdate( double horvel )
	{
		boolean underControl = false;

		// START outer forces
		final byte b0 = 5;
		final int bpermeter = (int)( b0 * ( getBoundingBox().maxY - getBoundingBox().minY ) );
		float watervolume = 0F;
		final AxisAlignedBB axisalignedbb = new AxisAlignedBB( 0D, 0D, 0D, 0D, 0D, 0D );
		int belowwater = 0;
		for( ; belowwater < bpermeter; belowwater++ )
		{
			final double d1 = getBoundingBox().minY + ( getBoundingBox().maxY - getBoundingBox().minY ) * belowwater / bpermeter;
			final double d2 = getBoundingBox().minY + ( getBoundingBox().maxY - getBoundingBox().minY ) * ( belowwater + 1 ) / bpermeter;
			setEntityBoundingBox( new AxisAlignedBB( getBoundingBox().minX, d1, getBoundingBox().minZ, getBoundingBox().maxX, d2,
					getBoundingBox().maxZ ) );

			if( !isAABBInLiquidNotFall( worldObj, axisalignedbb ) )
				break;
		}
		if( belowwater > 0 && layeredBlockVolumeCount != null )
		{
			final int k = belowwater / b0;
			for( int y = 0; y <= k && y < layeredBlockVolumeCount.length; y++ )
				if( y == k )
					watervolume += layeredBlockVolumeCount[y] * ( belowwater % b0 ) * MaterialDensity.WATER_DENSITY / b0;
				else
					watervolume += layeredBlockVolumeCount[y] * MaterialDensity.WATER_DENSITY;
		}

		if( onGround )
			isFlying = false;

		final float gravity = 0.05F;
		if( watervolume > 0F )
		{
			isFlying = false;
			final float buoyancyforce = MaterialDensity.WATER_DENSITY * watervolume * gravity; // F = rho * V * g (Archimedes' law)
			final float mass = capabilities.getMass();
			motionY += buoyancyforce / mass;
		}
		if( !isFlying() )
			motionY -= gravity;
		capabilities.updateEngines();
		// END outer forces

		// START player input
		if( riddenByEntity == null )
			if( prevRiddenByEntity != null )
			{
				if( ShipWright.instance.modConfig.disassembleOnDismount )
				{
					alignToGrid();
					updateRiderPosition( prevRiddenByEntity, new BlockPos( seatX, seatY, seatZ ), 1 );
					disassemble( false );
				}
				else
					if( !worldObj.isRemote && isFlying() )
					{
						final EntityParachute parachute = new EntityParachute( worldObj, this, seatX, seatY, seatZ );
						if( worldObj.spawnEntityInWorld( parachute ) )
						{
							prevRiddenByEntity.mountEntity( parachute );
							prevRiddenByEntity.setSneaking( false );
						}
					}
				prevRiddenByEntity = null;
			}

		if( riddenByEntity == null )
		{
			if( isFlying() )
				motionY -= BASE_LIFT_SPEED * 0.2F;
		}
		else
		{
			underControl = handlePlayerControl();
			prevRiddenByEntity = riddenByEntity;
		}
		if( !underControl )
			driftToGrid();
		// END player input

		// START limit motion
		double newhorvel = Math.sqrt( motionX * motionX + motionZ * motionZ );
		final double maxvel = ShipWright.instance.modConfig.speedLimit;
		if( newhorvel > maxvel )
		{
			final double d = maxvel / newhorvel;
			motionX *= d;
			motionZ *= d;
			newhorvel = maxvel;
		}
		motionY = MathHelperMod.clamp_double( motionY, -maxvel, maxvel );
		// END limit motion

		if( onGround )
		{
			motionX *= groundFriction;
			motionY *= groundFriction;
			motionZ *= groundFriction;
		}
		rotationPitch = rotationPitch + ( motionYaw * ShipWright.instance.modConfig.bankingMultiplier - rotationPitch ) * 0.15f;
		motionYaw *= 0.7F;
		// motionYaw = MathHelper.clamp_float(motionYaw, -BASE_TURN_SPEED * ShipMod.instance.modConfig.turnSpeed, BASE_TURN_SPEED *
		// ShipMod.instance.modConfig.turnSpeed);
		rotationYaw += motionYaw;
		setRotatedBoundingBox();
		moveEntity( motionX, motionY, motionZ );
		posY = Math.min( posY, worldObj.getHeight() );
		motionX *= horFriction;
		motionY *= vertFriction;
		motionZ *= horFriction;

		if( ShipWright.instance.modConfig.shipControlType == ModSettings.CONTROL_TYPE_VANILLA )
		{
			double newyaw = rotationYaw;
			final double dx = prevPosX - posX;
			final double dz = prevPosZ - posZ;

			if( riddenByEntity != null && !isBraking() && dx * dx + dz * dz > 0.01D )
				newyaw = 270F - Math.toDegrees( Math.atan2( dz, dx ) ) + frontDirection * 90F;

			double deltayaw = MathHelper.wrapAngleTo180_double( newyaw - rotationYaw );
			final double maxyawspeed = 2D;
			if( deltayaw > maxyawspeed )
				deltayaw = maxyawspeed;
			if( deltayaw < -maxyawspeed )
				deltayaw = -maxyawspeed;

			rotationYaw = (float)( rotationYaw + deltayaw );
		}
		setRotation( rotationYaw, rotationPitch );

		// START Collision
		if( !worldObj.isRemote )
		{
			@SuppressWarnings( "unchecked" )
			final List< Entity > list = worldObj.getEntitiesWithinAABBExcludingEntity( this, getBoundingBox().expand( 0.2D, 0.0D, 0.2D ) );
			if( list != null && !list.isEmpty() )
				for( final Entity entity : list )
					if( entity != riddenByEntity && entity.canBePushed() )
						if( entity instanceof EntityShip )
							entity.applyEntityCollision( this );
						else
							if( entity instanceof EntityBoat )
							{
								double d0 = posX - entity.posX;
								double d1 = posZ - entity.posZ;
								double d2 = MathHelper.abs_max( d0, d1 );

								if( d2 >= 0.01D )
								{
									d2 = MathHelper.sqrt_double( d2 );
									d0 /= d2;
									d1 /= d2;
									double d3 = 1.0D / d2;

									if( d3 > 1.0D )
										d3 = 1.0D;

									d0 *= d3;
									d1 *= d3;
									d0 *= 0.05D;
									d1 *= 0.05D;
									d0 *= 1.0F - entity.entityCollisionReduction;
									d1 *= 1.0F - entity.entityCollisionReduction;
									entity.addVelocity( -d0, 0.0D, -d1 );
								}
							}

			for( int l = 0; l < 4; ++l )
			{
				final int i1 = MathHelper.floor_double( posX + ( l % 2 - 0.5D ) * 0.8D );
				final int j1 = MathHelper.floor_double( posZ + ( l / 2 - 0.5D ) * 0.8D );

				for( int k1 = 0; k1 < 2; ++k1 )
				{
					final int l1 = MathHelper.floor_double( posY ) + k1;
					final IBlockState blockState = worldObj.getBlockState( new BlockPos( i1, l1, j1 ) );
					final Block block = blockState.getBlock();

					if( block == Blocks.snow )
					{
						worldObj.setBlockToAir( new BlockPos( i1, l1, j1 ) );
						isCollidedHorizontally = false;
					}
					else
						if( block == Blocks.waterlily )
						{
							worldObj.destroyBlock( new BlockPos( i1, l1, j1 ), true );
							isCollidedHorizontally = false;
						}
				}
			}
		}
		// END Collision
	}

	private boolean handlePlayerControl()
	{
		boolean underControl = false;

		if( riddenByEntity instanceof EntityLivingBase )
		{
			double throttle = ( (EntityLivingBase)riddenByEntity ).moveForward;
			if( isFlying() )
				throttle *= 0.5D;
			if( throttle > 0.0D )
				underControl = true;

			if( ShipWright.instance.modConfig.shipControlType == ModSettings.CONTROL_TYPE_ARCHIMEDES )
			{
				Vec3 vec = new Vec3( riddenByEntity.motionX, 0D, riddenByEntity.motionZ );
				vec = vec.rotateYaw( (float)Math.toRadians( riddenByEntity.rotationYaw ) );

				final double steer = ( (EntityLivingBase)riddenByEntity ).moveStrafing;
				if( steer != 0.0D )
					underControl = true;

				motionYaw += steer * BASE_TURN_SPEED * capabilities.getPoweredRotationMult() * ShipWright.instance.modConfig.turnSpeed;

				//@formatter:off
				/*
				float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection * 90F);
				vec.xCoord = motionX;
				vec.zCoord = motionZ;
				vec = vec.rotateYaw(yaw);
				vec.xCoord *= 0.9D;
				vec.zCoord -= throttle * BASE_FORWARD_SPEED * capabilities.getPoweredSpeedMult();
				vec = vec.rotateYaw(-yaw);
				 */
				//@formatter:on

				motionX = vec.xCoord;
				motionZ = vec.zCoord;
			}
			else
				if( ShipWright.instance.modConfig.shipControlType == ModSettings.CONTROL_TYPE_VANILLA )
					if( throttle > 0.0D )
					{
						final double dsin = -Math.sin( Math.toRadians( riddenByEntity.rotationYaw ) );
						final double dcos = Math.cos( Math.toRadians( riddenByEntity.rotationYaw ) );
						motionX += dsin * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
						motionZ += dcos * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
					}
		}

		if( controller.getShipControl() != 0 )
		{
			if( controller.getShipControl() == 4 )
				alignToGrid();
			else
				if( isBraking() )
				{
					motionX *= capabilities.brakeMult;
					motionZ *= capabilities.brakeMult;
					if( isFlying() )
						motionY *= capabilities.brakeMult;
				}
				else
					if( controller.getShipControl() < 3 && capabilities.canFly() )
					{
						int i;
						if( controller.getShipControl() == 2 )
						{
							isFlying = true;
							i = 1;
						}
						else
							i = -1;
						motionY += i * BASE_LIFT_SPEED * capabilities.getPoweredLiftMult();
					}
			underControl = true;
		}
		return underControl;
	}

	@Override
	public boolean handleWaterMovement()
	{
		final float f = width;
		width = 0F;
		final boolean ret = super.handleWaterMovement();
		width = f;
		return ret;
	}

	public boolean isFlying()
	{
		return capabilities.canFly() && ( isFlying || controller.getShipControl() == 2 );
	}

	public boolean isBraking()
	{
		return controller.getShipControl() == 3;
	}

	/**
	 * Determines whether the entity should be pushed by fluids
	 */
	@Override
	public boolean isPushedByWater()
	{
		return ticksExisted > 60;
	}

	@SideOnly( Side.CLIENT )
	protected void spawnParticles( double horvel )
	{
		//@formatter:off
		/*if (isInWater() && horvel > 0.1625D)
		{
			/*double yaw = Math.toRadians(rotationYaw);
			double cosyaw = Math.cos(yaw);
			double sinyaw = Math.sin(yaw);*//*

											for (int j = 0; j < 1D + horvel * 60D; j++)
											{
											worldObj.spawnParticle("splash", posX + (rand.nextFloat() - 0.5F) * width, posY, posZ + (rand.nextFloat() - 0.5F) * width, motionX, motionY + 1F, motionZ);
											}
											for (int j = 0; j < 1D + horvel * 20D; j++)
											{
											worldObj.spawnParticle("bubble", posX + rand.nextFloat() - 0.5F, posY - 0.2D, posZ + rand.nextFloat() - 0.5F, 0D, 0D, 0D);
											}
											}*/
		//@formatter:on
		if( capabilities.getEngines() != null )
		{
			// Vec3 vec = new Vec3(0d, 0d, 0d);
			final float yaw = (float)Math.toRadians( rotationYaw );
			for( final TileEntityEngine engine : capabilities.getEngines() )
				if( engine.isRunning() )
				{
					Vec3 vec = new Vec3( engine.getPos().getX() - shipChunk.getCenterX() + 0.5f, engine.getPos().getY(), engine.getPos().getZ()
							- shipChunk.getCenterZ() + 0.5f );
					// vec.xCoord = engine.xCoord - shipChunk.getCenterX() + 0.5f;
					// vec.yCoord = engine.yCoord;
					// vec.zCoord = engine.zCoord - shipChunk.getCenterZ() + 0.5f;
					vec = vec.rotateYaw( yaw );
					worldObj.spawnParticle( EnumParticleTypes.SMOKE_NORMAL, posX + vec.xCoord, posY + vec.yCoord + 1d, posZ + vec.zCoord, 0d, 0d, 0d );
					// worldObj.spawnParticle("smoke", posX + vec.xCoord, posY + vec.yCoord + 1d, posZ + vec.zCoord, 0d, 0d, 0d);
				}
		}
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void setIsBoatEmpty( boolean flag )
	{
		boatIsEmpty = flag;
	}

	@Override
	public boolean shouldRiderSit()
	{
		return true;
	}

	@Override
	public void updateRiderPosition()
	{
		updateRiderPosition( riddenByEntity, new BlockPos( seatX, seatY, seatZ ), 1 );
	}

	public void updateRiderPosition( Entity entity, BlockPos pos, int flags )
	{
		if( entity != null )
		{
			final float yaw = (float)Math.toRadians( rotationYaw );
			final float pitch = (float)Math.toRadians( rotationPitch );

			int x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
			if( ( flags & 1 ) == 1 )
			{
				if( frontDirection == 0 )
					z1 -= 1;
				else
					if( frontDirection == 1 )
						x1 += 1;
					else
						if( frontDirection == 2 )
							z1 += 1;
						else
							if( frontDirection == 3 )
								x1 -= 1;

				final IBlockState blockState = shipChunk.getBlockState( new BlockPos( x1, MathHelper.floor_double( y1 + getMountedYOffset()
						+ entity.getYOffset() ), z1 ) );
				final Block block = blockState.getBlock();
				if( block.isOpaqueCube() )
				{
					x1 = pos.getX();
					y1 = pos.getY();
					z1 = pos.getZ();
				}
			}

			final double yoff = ( flags & 2 ) == 2 ? 0d : getMountedYOffset();
			Vec3 vec = new Vec3( x1 - shipChunk.getCenterX() + 0.5d, y1 - shipChunk.minY() + yoff, z1 - shipChunk.getCenterZ() + 0.5d );
			switch( frontDirection )
			{
			case 0:
				vec = vec.rotatePitch( -pitch );
				break;
			case 1:
				vec = vec.rotatePitch( pitch );
				break;
			case 2:
				vec = vec.rotatePitch( pitch );
				break;
			case 3:
				vec = vec.rotatePitch( -pitch );
				break;
			}
			vec = vec.rotateYaw( yaw );

			entity.setPosition( posX + vec.xCoord, posY + vec.yCoord + entity.getYOffset(), posZ + vec.zCoord );
		}
	}

	@Override
	public double getMountedYOffset()
	{
		return height * 0.75D + 0.5D;
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox( Entity entity )
	{
		return entity instanceof EntitySeat || entity.ridingEntity instanceof EntitySeat || entity instanceof EntityLiving ? null : entity
				.getBoundingBox();
		// return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox()
	{
		return getBoundingBox();
	}

	@Override
	public boolean canBePushed()
	{
		return onGround && !isInWater() && riddenByEntity == null;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	@Override
	public boolean attackEntityFrom( DamageSource source, float damage )
	{
		//@formatter:off
		/*if (source.isExplosion())
		{
			if (source.getEntity() != null && source.getEntity().getClass().getName().equals("ckathode.weaponmod.entity.projectile.EntityCannonBall"))
			{
				double dx = source.getEntity().posX - posX;
				double dy = source.getEntity().posY - posY;
				double dz = source.getEntity().posZ - posZ;

				Vec3 vec = worldObj.getWorldVec3Pool().getVecFromPool(dx, dy, dz);
				vec.rotateAroundY((float) Math.toRadians(-rotationYaw));

				worldObj.createExplosion(source.getEntity(), source.getEntity().posX, source.getEntity().posY, source.getEntity().posZ, 4F, false);
				source.getEntity().setDead();
			}
		}*/
		//@formatter:on
		return false;
	}

	// updateFallDistance()
	@Override
	protected void func_180433_a( double distancefallen, boolean onground, Block block, BlockPos blockPos )
	{
		if( !isFlying() )
		{

		}
	}

	@Override
	public void fall( float distance, float damageMultiplier )
	{}

	//@formatter:off
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.5F;
	}
	 */
	//@formatter:on

	public float getHorizontalVelocity()
	{
		return (float)Math.sqrt( motionX * motionX + motionZ * motionZ );
	}

	@Override
	public boolean interactFirst( EntityPlayer entityplayer )
	{
		return handler.interact( entityplayer );
	}

	public void alignToGrid()
	{
		rotationYaw = Math.round( rotationYaw / 90F ) * 90F;
		rotationPitch = 0F;

		Vec3 vec = new Vec3( -shipChunk.getCenterX(), -shipChunk.minY(), -shipChunk.getCenterZ() );
		vec = vec.rotateYaw( (float)Math.toRadians( rotationYaw ) );

		final int ix = MathHelperMod.round_double( vec.xCoord + posX );
		final int iy = MathHelperMod.round_double( vec.yCoord + posY );
		final int iz = MathHelperMod.round_double( vec.zCoord + posZ );

		posX = ix - vec.xCoord;
		posY = iy - vec.yCoord;
		posZ = iz - vec.zCoord;

		motionX = motionY = motionZ = 0D;
	}

	public void driftToGrid()
	{
		if( Math.abs( motionYaw ) < BASE_TURN_SPEED * 0.25f )
		{
			final float targetYaw = Math.round( rotationYaw / 90F ) * 90F - rotationYaw;
			final float targetDir = Math.min( Math.abs( targetYaw ), BASE_TURN_SPEED * 0.25f ) * Math.signum( targetYaw );
			motionYaw = targetDir;
		}

		if( Math.abs( motionX ) < BASE_FORWARD_SPEED * 0.25f && Math.abs( motionZ ) < BASE_FORWARD_SPEED * 0.25f )
		{
			Vec3 size = new Vec3( shipChunk.getSizeX(), shipChunk.getSizeY(), shipChunk.getSizeZ() );
			size = size.rotateYaw( (float)Math.toRadians( rotationYaw ) );

			final Vec3 target = new Vec3( getBlockAt( posX, size.xCoord ), getBlockAt( posY, size.yCoord ), getBlockAt( posZ, size.zCoord ) );
			final double ix = target.xCoord - posX;
			final double iy = target.yCoord - posY;
			final double iz = target.zCoord - posZ;

			final double targetX = Math.min( Math.abs( ix ), BASE_FORWARD_SPEED * 0.25f ) * Math.signum( ix );
			final double targetY = Math.min( Math.abs( iy ), BASE_FORWARD_SPEED * 0.25f ) * Math.signum( iy );
			final double targetZ = Math.min( Math.abs( iz ), BASE_FORWARD_SPEED * 0.25f ) * Math.signum( iz );

			motionX = targetX;
			motionZ = targetZ;
		}
	}

	public double getBlockAt( double x, double width )
	{
		return (int)x + width % 2 * 0.5;
	}

	public boolean disassemble( boolean overwrite )
	{
		if( worldObj.isRemote )
			return true;

		updateRiderPosition();

		final ChunkDisassembler disassembler = getDisassembler();
		disassembler.overwrite = overwrite;

		if( !disassembler.canDisassemble() )
		{
			if( prevRiddenByEntity instanceof EntityPlayer )
			{
				final ChatComponentText c = new ChatComponentText( "Cannot disassemble ship here" );
				( (EntityPlayer)prevRiddenByEntity ).addChatMessage( c );
			}
			return false;
		}

		final AssembleResult result = disassembler.doDisassemble();
		if( result.getShipMarker() != null )
		{
			final TileEntity te = result.getShipMarker().tileEntity;
			if( te instanceof TileEntityHelm )
			{
				( (TileEntityHelm)te ).setAssembleResult( result );
				( (TileEntityHelm)te ).setShipInfo( info );
			}
		}

		return true;
	}

	public void dropAsItems()
	{
		TileEntity tileentity;
		IBlockState blockState;
		Block block;
		for( int i = shipChunk.minX(); i < shipChunk.maxX(); i++ )
			for( int j = shipChunk.minY(); j < shipChunk.maxY(); j++ )
				for( int k = shipChunk.minZ(); k < shipChunk.maxZ(); k++ )
				{
					tileentity = shipChunk.getTileEntity( new BlockPos( i, j, k ) );
					if( tileentity instanceof IInventory )
					{
						final IInventory inv = (IInventory)tileentity;
						for( int it = 0; it < inv.getSizeInventory(); it++ )
						{
							final ItemStack is = inv.getStackInSlot( it );
							if( is != null )
								entityDropItem( is, 0F );
						}
					}
					blockState = shipChunk.getBlockState( new BlockPos( i, j, k ) );
					block = blockState.getBlock();

					if( block != Blocks.air )
						block.dropBlockAsItem( worldObj,
								new BlockPos( MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ) ),
								blockState, 0 );
				}
	}

	void fillAirBlocks( Set< BlockPos > set, int x, int y, int z )
	{
		if( x < shipChunk.minX() - 1 || x > shipChunk.maxX() || y < shipChunk.minY() - 1 || y > shipChunk.maxY() || z < shipChunk.minZ() - 1
				|| z > shipChunk.maxZ() )
			return;
		final BlockPos pos = new BlockPos( x, y, z );
		if( set.contains( pos ) )
			return;

		set.add( pos );
		if( shipChunk.setBlockAsFilledAir( x, y, z ) )
		{
			fillAirBlocks( set, x, y + 1, z );
			// fillAirBlocks(set, x, y - 1, z);
			fillAirBlocks( set, x - 1, y, z );
			fillAirBlocks( set, x, y, z - 1 );
			fillAirBlocks( set, x + 1, y, z );
			fillAirBlocks( set, x, y, z + 1 );
		}
	}

	@Override
	protected void writeEntityToNBT( NBTTagCompound compound )
	{
		super.writeEntityToNBT( compound );
		final ByteArrayOutputStream baos = new ByteArrayOutputStream( shipChunk.getMemoryUsage() );
		final DataOutputStream out = new DataOutputStream( baos );
		try
		{
			ChunkIO.writeAll( out, shipChunk );
			out.flush();
			out.close();
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}
		compound.setByteArray( "chunk", baos.toByteArray() );
		compound.setByte( "seatX", (byte)seatX );
		compound.setByte( "seatY", (byte)seatY );
		compound.setByte( "seatZ", (byte)seatZ );
		compound.setByte( "front", (byte)frontDirection );

		if( !shipChunk.chunkTileEntityMap.isEmpty() )
		{
			final NBTTagList tileentities = new NBTTagList();
			for( final TileEntity tileentity : shipChunk.chunkTileEntityMap.values() )
			{
				final NBTTagCompound comp = new NBTTagCompound();
				tileentity.writeToNBT( comp );
				tileentities.appendTag( comp );
			}
			compound.setTag( "tileent", tileentities );
		}

		compound.setString( "name", info.shipName );
		if( info.owner != null )
			compound.setString( "owner", info.owner );
	}

	@Override
	protected void readEntityFromNBT( NBTTagCompound compound )
	{
		super.readEntityFromNBT( compound );
		final byte[] ab = compound.getByteArray( "chunk" );
		final ByteArrayInputStream bais = new ByteArrayInputStream( ab );
		final DataInputStream in = new DataInputStream( bais );
		try
		{
			ChunkIO.read( in, shipChunk );
			in.close();
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}
		if( compound.hasKey( "seat" ) )
		{
			final short s = compound.getShort( "seat" );
			seatX = s & 0xF;
			seatY = s >>> 4 & 0xF;
				seatZ = s >>> 8 & 0xF;
		frontDirection = s >>> 12 & 3;
		}
		else
		{
			seatX = compound.getByte( "seatX" ) & 0xFF;
			seatY = compound.getByte( "seatY" ) & 0xFF;
			seatZ = compound.getByte( "seatZ" ) & 0xFF;
			frontDirection = compound.getByte( "front" ) & 3;
		}

		final NBTTagList tileentities = compound.getTagList( "tileent", 10 );
		if( tileentities != null )
			for( int i = 0; i < tileentities.tagCount(); i++ )
			{
				final NBTTagCompound comp = tileentities.getCompoundTagAt( i );
				final TileEntity tileentity = TileEntity.createAndLoadEntity( comp );
				shipChunk.setTileEntity( tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity.getPos().getZ(), tileentity );
			}

		info = new ShipInfo();
		info.shipName = compound.getString( "name" );
		if( compound.hasKey( "owner" ) )
			info.shipName = compound.getString( "owner" );
	}

	@Override
	public void writeSpawnData( ByteBuf data )
	{
		data.writeByte( seatX );
		data.writeByte( seatY );
		data.writeByte( seatZ );
		data.writeByte( frontDirection );

		data.writeShort( info.shipName.length() );
		data.writeBytes( info.shipName.getBytes() );

		try
		{
			ChunkIO.writeAllCompressed( data, shipChunk );
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}
		catch( final ShipSizeOverflowException ssoe )
		{
			disassemble( false );
			LogManager.getLogger( ModInfo.MODID ).warn( "Ship is too large to be sent" );
		}
	}

	@Override
	public void readSpawnData( ByteBuf data )
	{
		seatX = data.readUnsignedByte();
		seatY = data.readUnsignedByte();
		seatZ = data.readUnsignedByte();
		frontDirection = data.readUnsignedByte();

		final byte[] ab = new byte[data.readShort()];
		data.readBytes( ab );
		info.shipName = new String( ab );
		try
		{
			ChunkIO.readCompressed( data, shipChunk );
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}

		shipChunk.onChunkLoad();
	}
}
