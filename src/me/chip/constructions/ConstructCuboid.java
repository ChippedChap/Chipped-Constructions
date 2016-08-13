package me.chip.constructions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class ConstructCuboid {
	private Integer[] constructWidths = new Integer [3];
	private List<ConstructBlock> blockList = new ArrayList<ConstructBlock>();
	private Server serverInstance;

	/**
	 * Constructs a ConstructCuboid using data taken from a config file.
	 * @param firstPos 
	 * @param secondPos
	 * @param inputW
	 * @param includeAir
	 * @param currentServer
	 */
	ConstructCuboid (Integer [] firstPos, Integer [] secondPos, World inputW, Boolean includeAir, Server currentServer) {
		// Width Calculations
		int MISSED_BLOCK_HALF_COMPENSATION = 1;
		constructWidths[0] = Math.abs(secondPos[0] - firstPos [0] + MISSED_BLOCK_HALF_COMPENSATION);
		constructWidths[1] = Math.abs(secondPos[1] - firstPos [1] + MISSED_BLOCK_HALF_COMPENSATION);
		constructWidths[2] = Math.abs(secondPos[2] - firstPos [2] + MISSED_BLOCK_HALF_COMPENSATION);
		// Makes list of blocks
		for (int x = firstPos[0]; x <= secondPos[0]; x++) {
			for (int y = firstPos[1]; y <= secondPos[1]; y++) {
				for (int z = firstPos[2]; z <= secondPos[2]; z++) {
					Location blockLocation = new Location (inputW, x, y, z);
					if (!includeAir && !blockLocation.getBlock().isEmpty()) {
						blockList.add(new ConstructBlock (firstPos, blockLocation.getBlock()));
					} else {
						blockList.add(new ConstructBlock (firstPos, blockLocation.getBlock()));
					}
				}
			}
		}
		serverInstance = currentServer;
	}
	
	/**
	 * Constructs an EMPTY construct with no blocks in it.
	 * Use addBlock() to add blocks.
	 * @param widths
	 * @param currentServer
	 */
	ConstructCuboid (List<Integer> widths, Server currentServer) {
		constructWidths[0] = widths.get(0);
		constructWidths[1] = widths.get(1);
		constructWidths[2] = widths.get(2);
		serverInstance = currentServer;
	}
	
	public Integer[] getConstructWidths () {
		return constructWidths;
	}
	
	/**
	 * Adds a block to a ConstructCuboid
	 * @param blockToBeAdded
	 */
	public void addBlock (ConstructBlock blockToBeAdded) {
		blockList.add(blockToBeAdded);
	}
	
	/**
	 * Places the construct
	 * @param spawnFrom
	 * @return A Debug message
	 */
	public String placeConstruct (Location spawnFrom) {
		for (ConstructBlock placee : blockList) {
			placee.parseAbsolute(spawnFrom).getBlock().setType(placee.returnMaterial());
		}
		return "Construct placed!";
	}
	
	/**
	 * Get number of blocks
	 * @return Size of list containing Blocks
	 */
	public Integer getConstructBlockNum () {
		return (Integer) blockList.size();
	}
	
	/**
	 * Get an array of all the blocks
	 * @return output
	 */
	public ConstructBlock[] getConstructBlocks () {
		ConstructBlock[] output = blockList.toArray(new ConstructBlock[blockList.size()]);
		return output;
	}
	
	/**
	 * Uses a rotation matrix operation to rotate the Construct
	 * @return result
	 */
	public String rotateConstructClockwise () {
		String result = "";
		if (!new Integer (blockList.size()).equals(0)) {
			for (ConstructBlock rotatee : blockList) {
				int SIN_90_DEG = 1;
				int COS_90_DEG = 0;
				int newX = (COS_90_DEG * rotatee.relativePosition[0]) + (-SIN_90_DEG * rotatee.relativePosition[0]);
				int newY = (SIN_90_DEG * rotatee.relativePosition[2]) + (COS_90_DEG * rotatee.relativePosition[2]);
				rotatee.relativePosition[0] = newX;
				rotatee.relativePosition[2] = newY;
			}
			result = "Rotations successful.";
		} else {
			result = "Construct has no blocks!";
		}
		return result;
	}
	
	/**
	 * Centers the construct along the x and y axes.
	 * @return result
	 */
	public void setCenterIgnoringHeight () {
		Integer xaverage = 0;
		Integer zaverage = 0;
		for (ConstructBlock readingBlock : blockList) {
			xaverage += readingBlock.relativePosition[0];
			zaverage += readingBlock.relativePosition[2];
		}
		xaverage = (int) Math.floor(xaverage / blockList.size());
		zaverage = (int) Math.floor(zaverage / blockList.size());
		//Check if cuboid is already centered
		if (xaverage.equals(0) && zaverage.equals(0)) {
			serverInstance.getLogger().info("Plugin attempted to recenter a construct, however it was already centered.");
		} else {
			int xShiftFactor = 0;
			int zShiftFactor = 0;
			if (xaverage > 0) {
				xShiftFactor = -(int) Math.floor(constructWidths[0] / 2);

			} else if (xaverage < 0) {
				xShiftFactor = (int) Math.floor(constructWidths[0] / 2);
			}
			if (zaverage > 0) {
				zShiftFactor = -(int) Math.floor(constructWidths[2] / 2);
			} else if (zaverage < 0) {
				zShiftFactor = (int) Math.floor(constructWidths[2] / 2);
			}
			for (ConstructBlock blockToBeMoved : blockList) {
				blockToBeMoved.addCoords(xShiftFactor, 0, zShiftFactor);
			}
			serverInstance.getLogger().info("Recentered with a xShiftFactor of " + xShiftFactor + " and a zShiftFactor of " + zShiftFactor);
		}
	}
	
	/**
	 * Raises ConstructCuboid so that all the ConstructBlock's heights are nonzero.
	 */
	public void makeHeightsStartFromZero () {
		Integer lowestHeightValue = this.getLowestHeightValue();
			if (this.getLowestHeightValue() <= 0) {
				for (ConstructBlock blockBeingRaised : blockList) {
					blockBeingRaised.addCoords(0, -lowestHeightValue, 0);
				}
			} else {
				for (ConstructBlock blockBeingRaised : blockList) {
					blockBeingRaised.addCoords(0, lowestHeightValue, 0);
				}
			}
	}
	
	
	/**
	 * Gets the lowest height value of a ConstructCuboid
	 * @return lowestHeightValue
	 */
	public Integer getLowestHeightValue () {
		List<Integer> listOfHeightValues = new ArrayList<Integer>();
		for (ConstructBlock blockBeingChecked : blockList) {
			listOfHeightValues.add(blockBeingChecked.relativePosition[1]);
		}
		Integer lowestHeightValue = Collections.min(listOfHeightValues);;
		return lowestHeightValue;
	}
}