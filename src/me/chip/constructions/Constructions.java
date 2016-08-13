package me.chip.constructions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public final class Constructions extends JavaPlugin {
	@Override
	public void onEnable () {
		createConstructionsConfig();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public boolean onCommand (CommandSender sender, Command command, String label, String [] args) {
		Player playerSender = (Player) sender;
		// readconstruction:
		if (command.getName().equalsIgnoreCase("readconstruction")) {
			try {
				Integer[] pos1 = {Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])};
				Integer[] pos2 = {Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7])};
				readConstruction (playerSender, args [0], Boolean.valueOf(args [1]), pos1, pos2);
			} catch (Exception e) {
				playerSender.sendMessage(ChatColor.RED + "An error occured while parsing your input");
				playerSender.sendMessage(ChatColor.RED + e.toString());
				e.printStackTrace();
			}
			return true;
		}
		// placeconstruction:
		if (command.getName().equalsIgnoreCase("placeconstruction")) {
			try {
				String inputName = args [0];
				Block tgtBlock = getTargetBlock (playerSender, 8);
				getLogger().info(new StringBuilder (playerSender.getName()).append(" Attempted to spawn construct at ").append(tgtBlock.getX()).append(", ").append(tgtBlock.getY()).append(", ").append(tgtBlock.getZ()).toString());
				writeConstruction (playerSender, inputName, tgtBlock.getLocation().add(0, 1, 0));
			} catch (Exception e) {
				playerSender.sendMessage(ChatColor.RED + "An error occured while parsing your input");
				playerSender.sendMessage(ChatColor.RED + e.toString());
				e.printStackTrace();
			}
			return true;
		}
		// startglobalthermonuclearwar:
		if (command.getName().equalsIgnoreCase("startglobalthermonuclearwar")) {
			playerSender.sendMessage("How about a nice game of chess?");
			return true;
		}
		return false;
	}
	
	private void readConstruction (Player readingPlayer, String constructionName, Boolean groupPerms, Integer[] fvert, Integer[] svert) {
		if (fvert.equals(svert)) {
			readingPlayer.sendMessage(ChatColor.RED + "Your second position is the same is the first");
		} else {
			// Write data to config
			String baseDir = new StringBuilder("construct-dir.").append(constructionName.replaceAll(" ", "_").toLowerCase()).append(".").toString();
			getConfig().set(new StringBuilder(baseDir).append("name").toString(), constructionName);
			getConfig().set(new StringBuilder(baseDir).append("author").toString(), readingPlayer.getName());
			getConfig().set(new StringBuilder(baseDir).append("use_group_perms").toString(), groupPerms);
			getConfig().set(new StringBuilder(baseDir).append("group_name").toString(), "Hell's Grannies"); //TODO: IMPLEMENT GROUPS
			// Build Construct (THE NOUN) Cuboid and write it onto the config
			ConstructCuboid readConstruct = new ConstructCuboid(fvert, svert, readingPlayer.getWorld(), false, getServer());
			readConstruct.setCenterIgnoringHeight();
			readConstruct.makeHeightsStartFromZero();
			// Write construct widths
			getConfig().set(new StringBuilder(baseDir).append("widths").toString(), Arrays.asList(readConstruct.getConstructWidths()));
			// Index resets to -1 to start index from 0
			int index = 0;
			for (ConstructBlock currentBlock : readConstruct.getConstructBlocks()) {
				getConfig().set(new StringBuilder(baseDir).append(index ++).toString(), currentBlock.getDataAsList());
			}
			saveConfig();
			// Inform player of sucessful save
			readingPlayer.sendMessage(ChatColor.GREEN + "Save successful!");
		}
	}
	
	private void writeConstruction (Player writingPlayer, String requestedConstName, Location placeFrom) {
		String checkDir = new StringBuilder("construct-dir.").append(requestedConstName.replaceAll(" ", "_").toLowerCase()).toString();
		if (getConfig().contains(checkDir)) {
			// Groups will be implemented after read/write is completely done.
			if (writingPlayer.getName().equals(getConfig().getString(checkDir + ".name")) || writingPlayer.isOp()) {
				ConfigurationSection constructDataSection = getConfig().getConfigurationSection(checkDir);
				ConstructCuboid constructInWaiting = new ConstructCuboid (constructDataSection.getIntegerList("widths"), getServer());
				for (String currentKey : constructDataSection.getKeys(false)) {
					if (isInteger(currentKey)) {
						String blockDataDir = new StringBuilder(checkDir).append(".").append(currentKey).toString();
						List<String> recentlyReadInfo = getConfig().getStringList(blockDataDir);
						ConstructBlock blockAdee = new ConstructBlock(recentlyReadInfo.get(0), recentlyReadInfo.get(1), recentlyReadInfo.get(2), recentlyReadInfo.get(3));
						constructInWaiting.addBlock(blockAdee);
					}
				}
				// Place the construct!
				constructInWaiting.placeConstruct(placeFrom);
				writingPlayer.sendMessage(ChatColor.GREEN + "Construct placed!");
			} else {
				writingPlayer.sendMessage(ChatColor.RED + "You cannot use this command, as you are either not the author, not a part of a permitted group or not opped");
				return;
			}
		} else {
			writingPlayer.sendMessage(ChatColor.RED + "That construct was not found");
		}
	}
	
	private void createConstructionsConfig() {
		// From the spigot website, ensures that config.yml is present.
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				getLogger().info("config.yml not found, creating!");
				saveConfig();
			} else {
				getLogger().info("config.yml found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// This code is from Jonas Klemming on Stack Exchange. Thanks!
	public static final boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	// This code is from Clip on the Spigot forums, thanks!
    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
}