package me.NerdsWBNerds.SimpleWarps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleWarps extends JavaPlugin{
	public Logger log;
	
	public static ArrayList<Warp> warps = new ArrayList<Warp>();
	public static String prefix = ChatColor.GOLD + "[SimpleWarps] " + ChatColor.WHITE;
	
	public void onEnable(){
		log = getServer().getLogger();
		
		getServer().getPluginManager().registerEvents(new SWListener(this), this);
		
		loadWarps();
	}
	
	public void onDisable(){
		saveWarps();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("warp")){
				if(args.length == 0){
					if(!player.hasPermission("simplewarps.cmd.list")){
						player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.cmd.list to list warps.");
						return true;
					}
					
					String list = "";
					
					for(Warp w: warps){
						list += " *" + w.name;
					}
					
					player.sendMessage(ChatColor.DARK_AQUA + "** SIMPLEWARPS WARP LIST **");
					
					if(list.length() > 1)
						player.sendMessage(ChatColor.GREEN + list.substring(1));
					else
						player.sendMessage(ChatColor.GREEN + "There are currently no warps.");
					
					return true;
				}
				
				if(args.length == 1){
					if(!player.hasPermission("simplewarps.cmd.warp")){
						player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.cmd.warp to warp");
						return true;
					}
					
					if(!isWarp(args[0])){
						player.sendMessage(ChatColor.RED + "Error: The warp '" + args[0] + "' does not exist.");
						return true;
					}

					if(!player.hasPermission("simplewarps.warp." + args[0]) && !player.hasPermission("simplewarps.warp.*")){
						player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.warp." + args[0] + " to go to this warp.");
						return true;
					}
					
					player.teleport(getWarp(args[0]).location);
					player.sendMessage(prefix + ChatColor.GREEN + "You have been teleported to the " + ChatColor.AQUA + "'" + args[0] + "'" + ChatColor.GREEN + " warp.");
					
					return true;
				}
				
				if(args.length == 2){
					if(!player.hasPermission("simplewarps.cmd.warpother")){
						player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.cmd.warpother to warp other people.");
						return true;
					}
					
					Player target = getServer().getPlayer(args[0]);
					if(target == null || !target.isOnline()){
						player.sendMessage(ChatColor.RED + "Error: Player not found.");
						return true;
					}
					
					if(!isWarp(args[1])){
						player.sendMessage(ChatColor.RED + "Error: The warp '" + args[1] + "' does not exist.");
						return true;
					}

					if(!player.hasPermission("simplewarps.warp." + args[0])){
						player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.warp." + args[1] + " to send others to this warp.");
						return true;
					}
					
					target.teleport(getWarp(args[1]).location);
					player.sendMessage(prefix + ChatColor.GREEN + "You have teleported " + ChatColor.AQUA + "'" + target.getName() + "'" + ChatColor.GREEN + " to the " + ChatColor.AQUA + "'" + args[1] + "'" + ChatColor.GREEN + " warp.");
					target.sendMessage(prefix + ChatColor.GREEN + "You have been teleported to the " + ChatColor.AQUA + "'" + args[1] + "'" + ChatColor.GREEN + " warp.");
					
					return true;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("setwarp") && args.length == 1){
				if(!player.hasPermission("simplewarps.cmd.add")){
					player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.cmd.add to add warps.");
					return true;
				}
		
				if(args[0].length() > 13){
					player.sendMessage(ChatColor.RED + "Error: Warp names must be less than 14 characters long.");
					return true;
				}
				
				if(isWarp(args[0])){
					getWarp(args[0]).location = player.getLocation();
				}else{
					addWarp(args[0], player.getLocation());
				}
				
				player.sendMessage(prefix + ChatColor.AQUA + "'" + args[0] + "'" + ChatColor.GREEN + " warp set at your location.");
				saveWarps();
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("delwarp") && args.length == 1){
				if(!player.hasPermission("simplewarps.cmd.remove")){
					player.sendMessage(ChatColor.RED + "Error: You must have permission simplewarps.cmd.remove to delete warps.");
					return true;
				}
				
				if(!isWarp(args[0])){
					player.sendMessage(ChatColor.RED + "Error: The warp '" + args[0] + "' does not exist.");
					return true;
				}
				
				removeWarp(args[0]);
				player.sendMessage(prefix + ChatColor.AQUA + "'" + args[0] + "'" + ChatColor.GREEN + " warp has been removed.");
				saveWarps();
				return true;
			}
		}else{
			if(cmd.getName().equalsIgnoreCase("warp")){
				if(args.length == 0){
					String list = "";
					
					for(Warp w: warps){
						list += " *" + w.name;
					}
					
					consoleMessage(ChatColor.DARK_AQUA + "** SIMPLEWARPS WARP LIST **");
					consoleMessage(ChatColor.GREEN + list.substring(1));
					
					return true;
				}
				
				if(args.length == 2){
					Player target = getServer().getPlayer(args[0]);
					
					if(target == null || !target.isOnline()){
						consoleMessage(ChatColor.RED + "Error: Player not found.");
						return true;
					}
					
					if(!isWarp(args[1])){
						consoleMessage(ChatColor.RED + "Error: The warp '" + args[1] + "' does not exist.");
						return true;
					}

					target.teleport(getWarp(args[1]).location);
					consoleMessage(prefix + ChatColor.GREEN + "You have teleported " + ChatColor.AQUA + "'" + target.getName() + "'" + ChatColor.GREEN + " to the " + ChatColor.AQUA + "'" + args[1] + "'" + ChatColor.GREEN + " warp.");
					target.sendMessage(prefix + ChatColor.GREEN + "You have been teleported to the " + ChatColor.AQUA + "'" + args[1] + "'" + ChatColor.GREEN + " warp.");
					
					return true;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("delwarp") && args.length == 1){
				if(!isWarp(args[0])){
					consoleMessage(ChatColor.RED + "Error: The warp '" + args[0] + "' does not exist.");
					return true;
				}
				
				removeWarp(args[0]);
				consoleMessage(prefix + ChatColor.AQUA + "'" + args[0] + "'" + ChatColor.GREEN + " warp has been removed.");
				saveWarps();
				return true;
			}
		}
		
		return false;
	}

	public void consoleMessage(String m){
		Bukkit.getConsoleSender().sendMessage(m);
	}
	
	public static boolean isWarp(String s){
		for(Warp w: warps){
			if(w.name.equalsIgnoreCase(s))
				return true;
		}
		
		return false;
	}
	
	public static Warp getWarp(String s){
		if(isWarp(s)){
			for(Warp w: warps){
				if(w.name.equalsIgnoreCase(s))
					return w;
			}
		}
		
		return null;
	}
	
	public static void removeWarp(String s){
		if(isWarp(s)){
			warps.remove(getWarp(s));
		}
	}
	
	public static void addWarp(String s, Location l){
		warps.add(new Warp(s, l));
	}
	
	// ------------------- Files --------------- //
	
	public void saveWarps(){
		String fName = "warps.data";
		
		ArrayList<String> format = new ArrayList<String>();
		
		for(Warp w: warps){
			String toAdd = w.name;

			toAdd += "," + w.location.getWorld().getName();
			toAdd += "," + w.location.getBlockX();
			toAdd += "," + w.location.getBlockY();
			toAdd += "," + w.location.getBlockZ();
			toAdd += "," + w.location.getYaw();
			toAdd += "," + w.location.getPitch();
			
			format.add(toAdd);
		}
		
		File file = new File("plugins/SimpleWarps/" + fName);
		
		new File("plugins/").mkdir();
		new File("plugins/SimpleWarps/").mkdir();
		
	    if(!file.exists()){
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Simplewarps had an error saving warps.");
			}
	    }

		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
			oos.writeObject(format);
			oos.flush();
			oos.close();
		}catch(Exception e){
			System.out.println("Simplewarps had an error saving warps.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadWarps(){
		String fName = "warps.data";
		
		File file = new File("plugins/SimpleWarps/" + fName);
		
		if(file.exists()){
			try{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
				Object result = ois.readObject();
	
				ois.close();
				if(result != null){
					ArrayList<String> parse = (ArrayList<String>) result;
					
					for(String i: parse){
						try{
							String[] args = i.split(",");
							
							Location warpL = new Location(getServer().getWorld(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Float.parseFloat(args[5]), Float.parseFloat(args[6]));
							Warp warp = new Warp(args[0], warpL);
							
							warps.add(warp);
							
							ois.close();
						}catch(Exception e){
							System.out.println("Simplewarps had an error loading warps.");
						}
					}
				}
			}catch(Exception e){
				System.out.println("Simplewarps had an error loading warps.");
			}
		}
	}
}
