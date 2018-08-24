package org.mineplugin.fight;

import java.util.ArrayList;
import java.util.Random;
//import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public final class listener implements Listener{

	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		int id = block.getTypeId();
		if( id==54 || id==146 || id ==130)
		{
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClosed(InventoryCloseEvent e)
	    {
	        if(e.getInventory().getType() == InventoryType.CHEST)
	        {
	            ItemStack[] items = e.getInventory().getContents();
	 
	            for(ItemStack item : items)
	            {
	                if(item != null) { return; }
	            }
	            
	            Chest c = (Chest) e.getInventory().getHolder();
	            Location loc = c.getBlock().getLocation();
	            c.getBlock().setType(Material.STAINED_GLASS_PANE);
	            c.getBlock().setData((byte)1);
	            c.getWorld().playEffect(loc, Effect.ENDER_SIGNAL,31);
	            c.getWorld().playSound(loc,Sound.ENDERMAN_TELEPORT, 10, 1);
	        }
	    }
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e){
		if (e.getInventory().getHolder() instanceof Chest ){
			Random rand = new Random();
			Inventory inv = e.getInventory();

			for(ItemStack is : inv.getContents()) {
		        // is will be null if empty, so if it isn't null, we can stop looking and say false
		        if(is != null) {
		            return;
		        }
		    }
		    // This code will only be reached if the loop goes through the entire inventory without finding an item. That means it's empty, and we should return true
		 
			double chance = Math.random() * 100;
			{
				
				
				
				
				if ( chance < 40) {inv.setItem(rand.nextInt(27),new ItemStack(Material.APPLE,rand.nextInt(3)+1));}
				else if (chance < 80) {inv.setItem(rand.nextInt(27),new ItemStack(Material.COOKED_BEEF,rand.nextInt(5)+1));}
				
				if ( chance < 20) {inv.setItem(rand.nextInt(27),new ItemStack(Material.IRON_INGOT,rand.nextInt(5)+1));}
				else if ( chance < 40) {inv.setItem(rand.nextInt(27),new ItemStack(Material.GOLD_INGOT,rand.nextInt(5)+1));}
				else if ( chance>80 && chance < 100) 
				{
					ItemStack item = new ItemStack(Material.BOW);
		    		ItemMeta meta = item.getItemMeta();
		    		ArrayList<String> lore = new ArrayList<String>();
		    		lore.add("战地专用弓");
		    		meta.setLore(lore);
		    		item.setItemMeta(meta);
					inv.setItem(rand.nextInt(27),item);
					inv.setItem(rand.nextInt(27),new ItemStack(Material.INK_SACK,rand.nextInt(5)+1,(short)4));
					inv.setItem(rand.nextInt(27),new ItemStack(Material.FISHING_ROD,1));
				}
				
				if(chance < 5){inv.setItem(rand.nextInt(27),new ItemStack(Material.GOLDEN_APPLE,1,(short)1));}
				else if ( chance < 10) {inv.setItem(rand.nextInt(27),new ItemStack(Material.DIAMOND_BLOCK,1));}
				else if ( chance < 20) {inv.setItem(rand.nextInt(27),new ItemStack(Material.GOLD_BLOCK,rand.nextInt(2)+1));}
				else if ( chance < 30) {inv.setItem(rand.nextInt(27),new ItemStack(Material.ANVIL,1));}
				else if ( chance < 40) {inv.setItem(rand.nextInt(27),new ItemStack(Material.ENCHANTMENT_TABLE,1));}
				else if ( chance < 50) {inv.setItem(rand.nextInt(27),new ItemStack(Material.TNT,rand.nextInt(5)+1));}
				else if ( chance < 60) {inv.setItem(rand.nextInt(27),new ItemStack(Material.EXP_BOTTLE,rand.nextInt(32)+1));}
				else if ( chance < 70) {inv.setItem(rand.nextInt(27),new ItemStack(Material.DIAMOND,rand.nextInt(4)+1));}
				else if ( chance < 80) {inv.setItem(rand.nextInt(27),new ItemStack(Material.ARROW,rand.nextInt(15)+1));}
				else if ( chance < 90) {inv.setItem(rand.nextInt(27),new ItemStack(Material.ENDER_PEARL,rand.nextInt(2)+1));}

			}
        }
		return;
	}
	
	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent event) {

		double num = Math.random() * 100;
		
		{
			//Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',num+"&4 NO!"));
			if (num < 10)
			{
				int bx = event.getChunk().getX()<<4;
				int bz = event.getChunk().getZ()<<4;
				World world = event.getWorld();
				Random rand = new Random();
				int xx = rand.nextInt(17) + bx;
				int zz = rand.nextInt(17) + bz;
				int yy = world.getHighestBlockYAt(xx, zz);
				//Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',"x:"+xx+"y:"+yy+"z:"+zz+"&7 YES!"));
				world.getBlockAt(xx, yy, zz).setType(Material.CHEST);
				
				/*Block blc = world.getBlockAt(xx, yy, zz);
				if(blc.getState() instanceof Chest) 
				{
				Chest chest = (Chest) blc.getState();
				Inventory inv = chest.getInventory();*/
				//Material[] randomItens = {Material.APPLE, Material.DIAMOND};
				//ItemStack[] ISs = {new ItemStack(Material.APPLE,rand.nextInt(3)),new ItemStack(Material.DIAMOND,1),new ItemStack(Material.AIR,1),new ItemStack(Material.AIR,1),new ItemStack(Material.AIR,1),new ItemStack(Material.AIR,1),new ItemStack(Material.AIR,1)};
				
					//inv.setItem(i,new ItemStack(randomItens[rand.nextInt(randomItens.length)]));

				}
			
			}
		}

}