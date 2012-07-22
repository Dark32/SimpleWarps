package me.NerdsWBNerds.SimpleWarps.Commands;

import static org.bukkit.ChatColor.RED;
import me.NerdsWBNerds.SimpleWarps.SimpleWarps;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OtherCommand implements CommandExecutor{
	private SimpleWarps plugin;
	public OtherCommand(SimpleWarps s){
		plugin = s;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			//Player player = (Player) sender;		

		}else{

			//////////////// -------------------------------------------- /////////////////
			//////////////// ------------ CONSOLE COMMANDS -------------- /////////////////
			//////////////// -------------------------------------------- /////////////////

			return true;
		}
		
		return false;
	}
}
