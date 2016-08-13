package me.chip.constructions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ConstructBlock {
	public String material;
	public Integer[] relativePosition = new Integer[3];
	
	/**
	 * Constructs a new ConstructBlock for ordinary use
	 * @param relativeOrigin
	 * @param block
	 */
	ConstructBlock (Integer [] relativeOrigin, Block block) {
		// Sets relative position
		relativePosition[0] = (relativeOrigin [0]) - block.getX();
		relativePosition[1] = (relativeOrigin [1]) - block.getY();
		relativePosition[2] = (relativeOrigin [2]) - block.getZ();
		// Gets material
		material = block.getType().toString();
	}
	
	/**
	 * Constructs a new ConstructBlock using a raw list from the config
	 * @param x
	 * @param y
	 * @param z
	 * @param t
	 */
	ConstructBlock (String x, String y, String z, String t) {
		relativePosition[0] = Integer.parseInt(x);
		relativePosition[1] = Integer.parseInt(y);
		relativePosition[2] = Integer.parseInt(z);
		material = t;
	}
	
	/**
	 * Returns the material of this ConstructBlock
	 * @return
	 */
	public Material returnMaterial () {
		Material returnLoc = Material.getMaterial(material);
		return returnLoc;
	}
	
	
	/**
	 * Parses relative coordinates of the ConstructBlock into absolute coordinates based on a passed Location
	 * @param origin
	 * @return
	 */
	public Location parseAbsolute (Location origin) {
		Location absolute = new Location (origin.getWorld(), origin.getBlockX() + relativePosition [0], origin.getBlockY() + relativePosition [1], origin.getBlockZ() + relativePosition [2]);
		return absolute;
	}
	
	/**
	 * Adds to the ConstructBlock's relative position
	 * @param xoffset
	 * @param yoffset
	 * @param zoffset
	 */
	public void addCoords (int xoffset, int yoffset, int zoffset) {
		relativePosition[0] += xoffset;
		relativePosition[1] += yoffset;
		relativePosition[2] += zoffset;
	}
	
	
	/**
	 * Returns the ConstructBlock's relativePositions and material as a string
	 * @return
	 */
	public List<String> getDataAsList () {
		List<String> exportList = new ArrayList<String>();
		exportList.add(new StringBuilder(3).append(relativePosition[0]).toString());
		exportList.add(new StringBuilder(3).append(relativePosition[1]).toString());
		exportList.add(new StringBuilder(3).append(relativePosition[2]).toString());
		exportList.add(material.toString());
		return exportList;
	}
	
}
