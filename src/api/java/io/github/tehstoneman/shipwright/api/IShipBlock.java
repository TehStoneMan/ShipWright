package io.github.tehstoneman.shipwright.api;

/**
 * Describes a block that can be an active part of a ship.
 * 
 * @author TehStoneMan
 *
 */
public interface IShipBlock extends IBlockDensity
{
	/**
	 * Can this block be part of a ship?
	 * 
	 * Returning false will mean that this block can never be part of a ship.
	 * 
	 * @return true or false
	 */
	public boolean canConnectShip();

	/**
	 * Can this block support having other structure blocks connected to it?
	 * 
	 * A structure block can only be connected to other structure blocks, or to the ship's helm.
	 * A structure block extends the constructible range of a ship by a 5x5 area around it.
	 * 
	 * Returning false on a helm block means this this helm does not support structure blocks.
	 * 
	 * @return true or false
	 */
	public boolean isStructureBlock();

	/**
	 * Can this block connect to a ship beyond the range of a structure block?
	 * 
	 * A support block can be considered part of a ship, even outside the normal build range, as long as one side connects with part of a ship.
	 * Support blocks will also allow non-support blocks immediately adjacent to it to connect to the ship.
	 * 
	 * Blocks such as Balloons, Engines, Wings and decorative pieces should be considered as support blocks.
	 * 
	 * @return true or false
	 */
	public boolean isSupportBlock();
}
