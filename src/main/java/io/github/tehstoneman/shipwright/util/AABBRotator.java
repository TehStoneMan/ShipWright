package io.github.tehstoneman.shipwright.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AABBRotator
{
	private static Vec3	vec00, vec01, vec10, vec11;
	private static Vec3	vec0h, vec1h, vech0, vech1;
	static
	{
		vec00 = new Vec3(0D, 0D, 0D);
		vec01 = new Vec3(0D, 0D, 0D);
		vec10 = new Vec3(0D, 0D, 0D);
		vec11 = new Vec3(0D, 0D, 0D);
		
		vec0h = new Vec3(0D, 0D, 0D);
		vec1h = new Vec3(0D, 0D, 0D);
		vech0 = new Vec3(0D, 0D, 0D);
		vech1 = new Vec3(0D, 0D, 0D);
	}
	
	/**
	 * @param aabb
	 *            The axis aligned boundingbox to rotate
	 * @param ang
	 *            The angle to rotate the aabb in radians
	 */
	public static void rotateAABBAroundY(AxisAlignedBB aabb, double xoff, double zoff, float ang)
	{
		double y0 = aabb.minY;
		double y1 = aabb.maxY;

		vec00 = new Vec3(aabb.minX - xoff, vec00.yCoord,aabb.minZ - zoff );
		vec01 = new Vec3(aabb.minX - xoff, vec00.yCoord,aabb.maxZ - zoff );
		vec10 = new Vec3(aabb.maxX - xoff, vec10.yCoord,aabb.minZ - zoff );
		vec11 = new Vec3(aabb.maxX - xoff, vec11.yCoord,aabb.maxZ - zoff );
		
		vec00 = vec00.rotateYaw(ang);
		vec01 = vec01.rotateYaw(ang);
		vec10 = vec10.rotateYaw(ang);
		vec11 = vec11.rotateYaw(ang);
		
		vec0h = new Vec3((vec00.xCoord + vec01.xCoord) / 2D, vec0h.yCoord,(vec00.zCoord + vec01.zCoord) / 2D );
		vec1h = new Vec3((vec10.xCoord + vec11.xCoord) / 2D, vec1h.yCoord,(vec10.zCoord + vec11.zCoord) / 2D );
		vech0 = new Vec3((vec00.xCoord + vec10.xCoord) / 2D, vech0.yCoord,(vec00.zCoord + vec10.zCoord) / 2D );
		vech1 = new Vec3((vec01.xCoord + vec11.xCoord) / 2D, vech1.yCoord,(vec01.zCoord + vec11.zCoord) / 2D );
		
		aabb.fromBounds(minX(), y0, minZ(), maxX(), y1, maxZ()).offset(xoff, 0F, zoff);
	}
	
	private static double minX()
	{
		return Math.min(Math.min(Math.min(vec0h.xCoord, vec1h.xCoord), vech0.xCoord), vech1.xCoord);
	}
	
	private static double minZ()
	{
		return Math.min(Math.min(Math.min(vec0h.zCoord, vec1h.zCoord), vech0.zCoord), vech1.zCoord);
	}
	
	private static double maxX()
	{
		return Math.max(Math.max(Math.max(vec0h.xCoord, vec1h.xCoord), vech0.xCoord), vech1.xCoord);
	}
	
	private static double maxZ()
	{
		return Math.max(Math.max(Math.max(vec0h.zCoord, vec1h.zCoord), vech0.zCoord), vech1.zCoord);
	}
}
