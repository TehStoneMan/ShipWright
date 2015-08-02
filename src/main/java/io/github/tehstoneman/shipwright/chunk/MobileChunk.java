package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.entity.IShipTileEntity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class MobileChunk implements IBlockAccess
{
	public static final int								CHUNK_SIZE			= 16;
	public static final int								CHUNK_SIZE_EXP		= 4;
	public static final int								CHUNK_MEMORY_USING	= CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * (4 + 2);	//(16*16*16 shorts and ints)
																																
	protected final World								worldObj;
	protected final EntityShip							entityShip;
	private Map<BlockPos, ExtendedBlockStorage>	blockStorageMap;
	
	public Map<BlockPos, TileEntity>				chunkTileEntityMap;
	
	private boolean										boundsInit;
	private int											minX, minY, minZ, maxX, maxY, maxZ;
	private int											blockCount;
	
	public boolean										isChunkLoaded;
	public boolean										isModified;
	
	private BiomeGenBase								creationSpotBiome;
	
	public MobileChunk(World world, EntityShip entityship)
	{
		worldObj = world;
		entityShip = entityship;
		blockStorageMap = new HashMap<BlockPos, ExtendedBlockStorage>(1);
		chunkTileEntityMap = new HashMap<BlockPos, TileEntity>(2);
		
		isChunkLoaded = false;
		isModified = false;
		
		boundsInit = false;
		minX = minY = minZ = maxX = maxY = maxZ = -1;
		blockCount = 0;
		
		creationSpotBiome = BiomeGenBase.ocean;
	}
	
	public ExtendedBlockStorage getBlockStorage(int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x >> CHUNK_SIZE_EXP, y >> CHUNK_SIZE_EXP, z >> CHUNK_SIZE_EXP);
		return blockStorageMap.get(pos);
	}
	
	public ExtendedBlockStorage getBlockStorageOrCreate(int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x >> CHUNK_SIZE_EXP, y >> CHUNK_SIZE_EXP, z >> CHUNK_SIZE_EXP);
		ExtendedBlockStorage storage = blockStorageMap.get(pos);
		if (storage != null) return storage;
		storage = new ExtendedBlockStorage(pos.getY(), false);
		blockStorageMap.put(pos, storage);
		return storage;
	}
	
	public int getBlockCount()
	{
		return blockCount;
	}
	
	public float getCenterX()
	{
		return (minX + maxX) / 2F;
	}
	
	public float getCenterY()
	{
		return (minY + maxY) / 2F;
	}
	
	public float getCenterZ()
	{
		return (minZ + maxZ) / 2F;
	}

	public int getSizeX()
	{
		return (minX + maxX);
	}
	
	public int getSizeY()
	{
		return (minY + maxY);
	}
	
	public int getSizeZ()
	{
		return (minZ + maxZ);
	}

	public int minX()
	{
		return minX;
	}
	
	public int maxX()
	{
		return maxX;
	}
	
	public int minY()
	{
		return minY;
	}
	
	public int maxY()
	{
		return maxY;
	}
	
	public int minZ()
	{
		return minZ;
	}
	
	public int maxZ()
	{
		return maxZ;
	}
	
	public void setCreationSpotBiomeGen(BiomeGenBase biomegenbase)
	{
		creationSpotBiome = biomegenbase;
	}
	
	/*
	@Override
	public Block getBlock(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return Blocks.air;
		return storage.getBlockByExtId(x & 15, y & 15, z & 15);
	}
	*/
	
	/**
	 * Return the metadata corresponding to the given coordinates inside a chunk.
	 */
	/*
	@Override
	public IBlockState getBlockMetadata(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return 0;
		return storage.getExtBlockMetadata(x & 15, y & 15, z & 15);
	}
	*/

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		ExtendedBlockStorage storage = getBlockStorage(pos.getX(), pos.getY(), pos.getZ());
		if (storage == null) return Blocks.air.getDefaultState();
		return storage.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, Block block, IBlockState blockState)
	{
		if (block == null) return false;
		
		ExtendedBlockStorage storage = getBlockStorageOrCreate(x, y, z);
		int i = x & 15;
		int j = y & 15;
		int k = z & 15;
		
		Block currentblock = storage.getBlockByExtId(i, j, k);
		IBlockState currentmeta = storage.get(i, j, k);
		if (currentblock == block && currentmeta == blockState)
		{
			return false;
		}
		
		//storage.func_150818_a(i, j, k, block);
		storage.set(i, j, k, blockState);
		
		if (boundsInit)
		{
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			minZ = Math.min(minZ, z);
			maxX = Math.max(maxX, x + 1);
			maxY = Math.max(maxY, y + 1);
			maxZ = Math.max(maxZ, z + 1);
		} else
		{
			boundsInit = true;
			minX = x;
			minY = y;
			minZ = z;
			maxX = x + 1;
			maxY = y + 1;
			maxZ = z + 1;
		}
		blockCount++;
		setChunkModified();
		
		TileEntity tileentity;
		if (block.hasTileEntity(blockState))
		{
			tileentity = getTileEntity(new BlockPos(x, y, z));
			
			if (tileentity == null)
			{
				setTileEntity(x, y, z, tileentity);
			}
			
			if (tileentity != null)
			{
				tileentity.updateContainingBlockInfo();
				//tileentity.blockType = block;
				//tileentity.blockMetadata = blockState;
			}
		}
		
		return true;
	}
	
	public boolean setBlockMetadata(int x, int y, int z, IBlockState blockState)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return false;
		
		IBlockState currentmeta = storage.get(x, y & 15, z);
		if (currentmeta == blockState)
		{
			return false;
		}
		
		setChunkModified();
		storage.set(x & 15, y & 15, z & 15, blockState);
		Block block = storage.getBlockByExtId(x & 15, y & 15, z & 15);
		
		if (block != null && block.hasTileEntity(blockState))
		{
			TileEntity tileentity = getTileEntity(new BlockPos(x, y, z));
			
			if (tileentity != null)
			{
				tileentity.updateContainingBlockInfo();
				//tileentity.blockMetadata = blockState;
			}
		}
		
		return true;
	}
	
	public boolean setBlockAsFilledAir(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return true;
		
		Block block = storage.getBlockByExtId(x & 15, y & 15, z & 15);
		int meta = storage.getExtBlockMetadata(x & 15, y & 15, z & 15);
		if (block == Blocks.air && meta == 1)
		{
			return true;
		}
		if (block == null || block.isAir(worldObj, new BlockPos(x, y, z)))
		{
			//storage.func_150818_a(x & 15, y & 15, z & 15, Blocks.air);
			storage.set(x & 15, y & 15, z & 15, Blocks.air.getDefaultState());
			onSetBlockAsFilledAir(x, y, z);
			return true;
		}
		return false;
	}
	
	protected void onSetBlockAsFilledAir(int x, int y, int z)
	{
	}
	
	/**
	 * Gets the TileEntity for a given block in this chunk
	 */
	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		TileEntity tileentity = chunkTileEntityMap.get(pos);
				
		if (tileentity == null)
		{
			IBlockState blockState = getBlockState(pos);
			Block block = blockState.getBlock();
			
			if (block == null || !block.hasTileEntity(blockState))
			{
				return null;
			}
			
			tileentity = block.createTileEntity(worldObj, blockState);
			setTileEntity(pos.getX(), pos.getY(), pos.getZ(), tileentity);
			
			tileentity = chunkTileEntityMap.get(pos);
		}
		
		return tileentity;
	}
	
	public void setTileEntity(int x, int y, int z, TileEntity tileentity)
	{
		if (tileentity == null)
		{
			return;
		}
		
		setChunkBlockTileEntity(x, y, z, tileentity);
	}
	
	/**
	 * Sets the TileEntity for a given block in this chunk
	 */
	private void setChunkBlockTileEntity(int x, int y, int z, TileEntity tileentity)
	{
		BlockPos pos = new BlockPos(x, y, z);
		tileentity.setWorldObj(worldObj);
		int ox = tileentity.getPos().getX();
		int oy = tileentity.getPos().getY();
		int oz = tileentity.getPos().getZ();
		tileentity.setPos( pos);
		
		IBlockState blockState = getBlockState(pos);
		Block block = blockState.getBlock();
		if (block != null && block.hasTileEntity(blockState))
		{
			//tileentity.blockMetadata = getBlockMetadata(x, y, z);
			tileentity.invalidate();
			chunkTileEntityMap.put(pos, tileentity);
			
			if (tileentity instanceof IShipTileEntity)
			{
				((IShipTileEntity) tileentity).setParentShip(entityShip, ox, oy, oz);
			}
		}
	}
	
	/**
	 * Adds a TileEntity to a chunk
	 */
	public void addTileEntity(TileEntity tileentity)
	{
		setChunkBlockTileEntity(tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity.getPos().getZ(), tileentity);
	}
	
	/**
	 * Removes the TileEntity for a given block in this chunk
	 */
	public void removeChunkBlockTileEntity(int x, int y, int z)
	{
		BlockPos BlockPos = new BlockPos(x, y, z);
		if (isChunkLoaded)
		{
			TileEntity tileentity = chunkTileEntityMap.remove(BlockPos);
			if (tileentity != null)
			{
				if (tileentity instanceof IShipTileEntity)
				{
					((IShipTileEntity) tileentity).setParentShip(null, x, y, z);
				}
				tileentity.invalidate();
			}
		}
	}
	
	/**
	 * Called when this Chunk is loaded by the ChunkProvider
	 */
	public void onChunkLoad()
	{
		isChunkLoaded = true;
		//worldObj.func_147448_a(chunkTileEntityMap.values());
	}
	
	/**
	 * Called when this Chunk is unloaded by the ChunkProvider
	 */
	public void onChunkUnload()
	{
		isChunkLoaded = false;
	}
	
	public void setChunkModified()
	{
		isModified = true;
	}
	
	/*
	@Override
	public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l)
	{
		int lv = EnumSkyBlock.Sky.defaultLightValue;
		return lv << 20 | l << 4;
	}
	*/
	
	@Override
	public boolean isAirBlock( BlockPos pos )
	{
		IBlockState blockState = getBlockState(pos);
		Block block = blockState.getBlock();
		return block == null || block.isAir(worldObj, pos);
	}
	
	public boolean isBlockTakingWaterVolume(BlockPos pos)
	{
		IBlockState blockState = getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == null || block.isAir(worldObj, pos))
		{
			//if (getBlockMetadata(x, y, z) == 1) return false;
		}
		return true;
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords( BlockPos pos )
	{
		return creationSpotBiome;
	}
	
	/*
	@Override
	public int getHeight()
	{
		return CHUNK_SIZE;
	}
	*/
	
	@Override
	public boolean extendedLevelsInChunkCache()
	{
		return false;
	}
	
	@Override
	public boolean isSideSolid( BlockPos pos, EnumFacing side, boolean _default )
	{
		if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000)
		{
			return _default;
		}
		
		IBlockState blockState = getBlockState(pos);
		Block block = blockState.getBlock();
		return block.isSideSolid(worldObj, pos, side);
	}
	
	/*
	@Override
	public int isBlockProvidingPowerTo(int i, int j, int k, int l)
	{
		return 0;
	}
	*/
	
	public final int getMemoryUsage()
	{
		return 2 + blockCount * 9; // (3 bytes + 2 bytes (short) + 4 bytes (int) = 9 bytes per block) + 2 bytes (short)
	}

	@Override
	public int getCombinedLight( BlockPos pos, int l )
	{
		int lv = EnumSkyBlock.SKY.defaultLightValue;
		return lv << 20 | l << 4;
	}

	@Override
	public int getStrongPower( BlockPos pos, EnumFacing direction )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public WorldType getWorldType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setBlockIDWithMetadata( BlockPos tPos, Block block, IBlockState blockMeta )
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setTileEntity( BlockPos tPos, TileEntity tileentity )
	{
		// TODO Auto-generated method stub
		
	}
}
