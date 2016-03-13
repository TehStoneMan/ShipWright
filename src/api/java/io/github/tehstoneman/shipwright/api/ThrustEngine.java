package io.github.tehstoneman.shipwright.api;

import net.minecraft.util.Vec3;

public interface ThrustEngine extends IShipBlock
{
	public Vec3 getThrustVector();
}
