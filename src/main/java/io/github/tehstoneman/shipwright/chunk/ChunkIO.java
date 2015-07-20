package io.github.tehstoneman.shipwright.chunk;

import io.github.tehstoneman.shipwright.ModInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import org.apache.logging.log4j.LogManager;

public abstract class ChunkIO
{
	public static void write(DataOutput out, MobileChunk chunk, Collection<BlockPos> blocks) throws IOException
	{
		out.writeShort(blocks.size());
		for (BlockPos p : blocks)
		{
			writeBlock(out, chunk, p.getX(), p.getY(), p.getZ());
		}
	}
	
	public static int writeAll(DataOutput out, MobileChunk chunk) throws IOException
	{
		int count = 0;
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					IBlockState blockState = chunk.getBlockState(new BlockPos(i, j, k));
					Block block = blockState.getBlock();
					if (block != Blocks.air)
					{
						count++;
					}
				}
			}
		}
		LogManager.getLogger( ModInfo.MODID ).debug("Writing mobile chunk data: " + count + " blocks");
		
		out.writeShort(count);
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					IBlockState blockState = chunk.getBlockState(new BlockPos(i, j, k));
					Block block = blockState.getBlock();
					if (block != Blocks.air)
					{
						writeBlock(out, Block.getIdFromBlock(block), chunk.getBlockState(new BlockPos(i, j, k)), i, j, k);
					}
				}
			}
		}
		
		return count;
		
	}
	
	public static void writeBlock(DataOutput out, MobileChunk chunk, int x, int y, int z) throws IOException
	{
		writeBlock(out, Block.getIdFromBlock(chunk.getBlockState(new BlockPos(x, y, z)).getBlock()), chunk.getBlockState(new BlockPos(x, y, z)), x, y, z);
	}
	
	public static void writeBlock(DataOutput out, int id, IBlockState blockState, int x, int y, int z) throws IOException
	{
		out.writeByte(x);
		out.writeByte(y);
		out.writeByte(z);
		out.writeShort(id);
		//out.writeInt(blockState);
	}
	
	public static void read(DataInput in, MobileChunk chunk) throws IOException
	{
		int count = in.readShort();
		
		LogManager.getLogger( ModInfo.MODID ).debug("Reading mobile chunk data: " + count + " blocks");
		
		int x, y, z;
		int id;
		int meta;
		for (int i = 0; i < count; i++)
		{
			x = in.readByte();
			y = in.readByte();
			z = in.readByte();
			id = in.readShort();
			meta = in.readInt();
			//chunk.setBlockIDWithMetadata(new BlockPos(x, y, z), Block.getBlockById(id), meta);
		}
	}
	
	public static void writeCompressed(ByteBuf buf, MobileChunk chunk, Collection<BlockPos> blocks) throws IOException
	{
		DataOutputStream out = preCompress(buf);
		write(out, chunk, blocks);
		postCompress(buf, out, blocks.size());
	}
	
	public static void writeAllCompressed(ByteBuf buf, MobileChunk chunk) throws IOException
	{
		DataOutputStream out = preCompress(buf);
		int count = writeAll(out, chunk);
		postCompress(buf, out, count);
	}
	
	private static DataOutputStream preCompress(ByteBuf data) throws IOException
	{
		ByteBufOutputStream bbos = new ByteBufOutputStream(data);
		DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bbos));
		return out;
	}
	
	private static void postCompress(ByteBuf data, DataOutputStream out, int count) throws IOException
	{
		out.flush();
		out.close();
		
		int byteswritten = data.writerIndex();
		float f = (float) byteswritten / (count * 9);
		LogManager.getLogger( ModInfo.MODID ).debug(String.format(Locale.ENGLISH, "%d blocks written. Efficiency: %d/%d = %.2f", count, byteswritten, count * 9, f));
		
		if (byteswritten > 32000)
		{
			LogManager.getLogger( ModInfo.MODID ).warn("Ship probably contains too many blocks");
		}
	}
	
	public static void readCompressed(ByteBuf data, MobileChunk chunk) throws IOException
	{
		DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(data)));
		read(in, chunk);
		in.close();
	}
}
