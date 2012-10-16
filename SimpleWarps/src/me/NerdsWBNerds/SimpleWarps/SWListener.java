package me.NerdsWBNerds.SimpleWarps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class SWListener implements Listener{
	public SimpleWarps plugin;
	
	public SWListener(SimpleWarps p){
		plugin = p;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.SIGN){
				Sign sign = (Sign) e.getClickedBlock().getState();
				
				if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[SIMPLEWARP]") || ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[SWARP]") || ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[WARP]")){
					String warp = ChatColor.stripColor(sign.getLine(1));
					
					if(!SimpleWarps.isWarp(warp)){
						sign.setLine(0, ChatColor.DARK_RED + "!ERROR!");
						sign.setLine(1, "warp does");
						sign.setLine(2, "not exist.");
						sign.setLine(3, "");
						return;
					}
					
					if(!e.getPlayer().hasPermission("simplewarps.sign.use")){
						e.getPlayer().sendMessage(ChatColor.RED + "Error: Using warp signs requires permission simplewarps.sign.use");
						return;
					}

					if(e.getPlayer().hasPermission("simplewarps.sign.all") || (e.getPlayer().hasPermission("simplewarps.warp." + warp) ||  e.getPlayer().hasPermission("simplewarps.warp.*"))){
						e.getPlayer().teleport(SimpleWarps.getWarp(warp).location);
						e.getPlayer().sendMessage(SimpleWarps.prefix + ChatColor.GREEN + "You have been teleported to the " + ChatColor.AQUA + "'" + warp + "'" + ChatColor.GREEN + " warp.");
					}else{
						e.getPlayer().sendMessage(ChatColor.RED + "Error: Using this warp sign requires permission simplewarps.sign.all or simplewarps.warp." + warp);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent e){
		Player player = e.getPlayer();
		
		if(e.getLine(0).equalsIgnoreCase("[SIMPLEWARP]") || e.getLine(0).equalsIgnoreCase("[SWARP]") || e.getLine(0).equalsIgnoreCase("[WARP]")){
			if(!player.hasPermission("simplewarps.sign.create")){
				e.setLine(0, ChatColor.DARK_RED + "!ERROR!");
				e.setLine(1, "you do not have");
				e.setLine(2, "permission to");
				e.setLine(3, "make warp signs");
				
				return;
			}

			if(!SimpleWarps.isWarp(e.getLine(1))){
				e.setLine(0, ChatColor.DARK_RED + "!ERROR!");
				e.setLine(1, "this warp");
				e.setLine(2, "does not");
				e.setLine(3, "exist");
			}else{
				e.setLine(0, ChatColor.WHITE + "[SIMPLEWARP]");
				e.setLine(1, ChatColor.BOLD + e.getLine(1));
				e.setLine(2, player.getName());
				e.setLine(3, ChatColor.DARK_GRAY + e.getLine(3));
				
				player.sendMessage(SimpleWarps.prefix + ChatColor.GREEN + "You have created a warp sign.");
			}
		}
	}
}
