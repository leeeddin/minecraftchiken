package org.mineplugin.fight;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;

public class fight extends JavaPlugin implements Listener{
	Map<String, Integer> killmap = new HashMap<String, Integer>();
	Map<String, Integer> endermap = new HashMap<String, Integer>();
	Map<String, Integer> ingame = new HashMap<String, Integer>();
	HashMap<String, Long> startTimes = new HashMap<String, Long>();
	HashMap<String, Long> lightstartTimes = new HashMap<String, Long>();
	int start = 0;
	int allowbreakopendie = 1;
    @Override
    public void onEnable() {
        getLogger().info("插件加载成功！!");
        getServer().getPluginManager().registerEvents(new listener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        //getServer().getPluginManager().registerEvents((new fight(), this);
    }

    @Override
    public void onDisable() {
    getLogger().info("onDisable has been invoked!");
    }
    int time;
    int bdtime;
    int taskID;
    int bdtaskID;
    public void setTimer(int amount) {
        time = amount;
    }
    public void startTimer(World world,int x,int z) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	if(time == 0) {
            		for (Player ps : Bukkit.getOnlinePlayers()) {
            			randomtp(ps,x,z,1000);
            			ingame.put(ps.getDisplayName(), 1);
            		}
            		allowbreakopendie = 1;
                    Bukkit.broadcastMessage(ChatColor.RED + "已传送!地面随机刷新资源箱！！地图中央上空有宝藏箱(末影箱)，获得极品装备!");
                    world.getBlockAt(x, 130, z).setType(Material.ENDER_CHEST);
                    stopTimer();
                    setbdTimer(1800);
    				startbdTimer(world.getWorldBorder());
    				Bukkit.broadcastMessage(ChatColor.GOLD+"本场比赛中心坐标为："+ChatColor.GREEN+"("+x+","+z+")"+"。"+ChatColor.GOLD+"边境为"+ChatColor.GREEN+"1000"+ChatColor.GOLD+"格，将在"+ChatColor.GREEN+"30"+ChatColor.GOLD+"分钟之内缩小到"+ChatColor.GREEN+"50"+ChatColor.GOLD+"格!请注意跑图方向!");
    				return;
                }
            	if(time < 10) {
                    //Bukkit.broadcastMessage(ChatColor.RED + "Timer remaining " + time + " seconds");
    				for (Player playerz : Bukkit.getOnlinePlayers()) {
    					IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + time+"\"}");
    					PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    					PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    					((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
    					((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
    				}
                }
            	else {
            		Bukkit.broadcastMessage(ChatColor.RED + "游戏还有："+ChatColor.BLUE+time+ChatColor.RED + "开始！");
            	}
            	time = time - 1;
            }
        }, 0L, 20L);

    }
    public void stopTimer() {

        Bukkit.getScheduler().cancelTask(taskID);
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
    {
    	if (cmd.getName().equalsIgnoreCase("startbf")) 
    	{
    		if (!(sender instanceof Player)) 
    		{
    			sender.sendMessage("This command can only be run by a player.");
    		} 
    		else 
    		{
    			Player player = (Player) sender;
    			
    			if(start==0) 
    			{
    				Location startloc = player.getLocation();
        			int srx = startloc.getBlockX();
        			int sry = startloc.getBlockY()+20;
        			int srz = startloc.getBlockZ();
        			World world = player.getWorld();
        			for(int xx = srx-16 ; xx<srx+16 ; xx++) 
        			{
        				for(int zz = srz-16 ; zz<srz+16 ; zz++) 
            			{
        					world.getBlockAt(xx, sry, zz).setType(Material.GLASS);
            			}
        			}
        			
        			Location tploc = new Location(world,srx,sry+3,srz);
    				for (Player playerz : Bukkit.getOnlinePlayers()) {
    					playerz.teleport(tploc);
    					killmap.put(playerz.getDisplayName(), 0);
    					endermap.put(playerz.getDisplayName(), 0);
    					playerz.setGameMode(GameMode.SURVIVAL);
    					IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "战地 " + ChatColor.GOLD + " 已开始！" + ChatColor.
RED + "即将将你TP到战场中！！ "+"\"}");
    					PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    					PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    					((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
    					((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
    					setsb(playerz);
    					playerz.getEquipment().clear();
                        playerz.getInventory().clear();
    				}
    				allowbreakopendie = 0;
    				player.sendMessage("游戏即将开始！");
    				start = 1;
    				
    				for(World wds :Bukkit.getWorlds()) {
    					WorldBorder wb = wds.getWorldBorder();
    					wb.setCenter(startloc);
    					wb.setSize(1000);
    					wb.setDamageAmount(0.1);
    					wb.setDamageBuffer(10);
    					wb.setWarningDistance(2);
    					wb.setWarningTime(5);
    					
    				}
    				setTimer(20);
    				startTimer(world,srx,srz);
    			}
    			else
    			{
    				player.sendMessage("游戏已经开始！");
    			}
    			}
    			// do something
    		return true;
    		}
    		
    	
    	return false;
    }
    public void startbdTimer(WorldBorder wb) {

   	 BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        bdtaskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
           	 wb.setSize(50+(double)((950.0/1800)*bdtime));
           	 bdtime = bdtime -1;
           	 if(bdtime == 1700||bdtime == 1600||bdtime == 1500||bdtime == 1400||bdtime == 1300||bdtime == 1200||bdtime == 1100||bdtime == 1000||bdtime == 900||bdtime == 800||bdtime == 700||bdtime == 600||bdtime == 500||bdtime == 400||bdtime == 300||bdtime == 200||bdtime == 100||bdtime == 0)
           	 {
           		Bukkit.broadcastMessage(ChatColor.RED+"边境已缩小为："+ChatColor.GREEN+(int)(50+(double)((950.0/1800)*bdtime))+ChatColor.RED+"格！");
           	 }
           	 if(bdtime == 0) {
           		Bukkit.broadcastMessage(ChatColor.RED+"边境已达到最小！");
           		stopbdTimer();
           	 }
         //  	 Bukkit.broadcastMessage(ChatColor.RED+""+bdtime);
            	}
            }, 0L, 20L);
   }
    public void setbdTimer(int amount) {

    	bdtime = amount;
    }
    public void stopbdTimer() {
    	Bukkit.getScheduler().cancelTask(bdtaskID);
    }
    public void randomtp(Player p,int x , int z , int bdsize) {
    	Random random = new Random();
    	int xx = random.nextInt(1000)+x-500;
    	int zz = random.nextInt(1000)+z-500;
    	int yy = p.getWorld().getHighestBlockYAt(xx, zz)+5;
    	Location tplo = new Location(p.getWorld(),xx,yy,zz);
    	p.teleport(tplo);
    	p.setGameMode(GameMode.SURVIVAL);
    	p.setHealth(20);
    	p.setFoodLevel(20);
		p.getEquipment().clear();
        p.getInventory().clear();
    }
    public void setsb(Player player) {

    	ScoreboardManager manager = Bukkit.getScoreboardManager();
    	Scoreboard board = manager.getNewScoreboard();
    	Objective objective = board.registerNewObjective("test", "dummy");
    	objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    	objective.setDisplayName(ChatColor.RED +"战   地");
    	Score info = objective.getScore(ChatColor.GREEN+"-----游戏规则-----");
    	info.setScore(999);
    	Score info2 = objective.getScore(ChatColor.LIGHT_PURPLE+"刺激的吃鸡游戏");
    	info2.setScore(998);
    	Score info3 = objective.getScore(ChatColor.LIGHT_PURPLE+"安全圈不断缩小");
    	info3.setScore(997);
    	Score info4 = objective.getScore(ChatColor.LIGHT_PURPLE+"击杀所有敌人！");
    	info4.setScore(996);
    	Score killt = objective.getScore(ChatColor.GREEN+"-----存活列表-----");
    	killt.setScore(995);
    	for (Player playerz : Bukkit.getOnlinePlayers()) {
    		objective.getScore(ChatColor.DARK_RED + playerz.getDisplayName()).setScore(0);
    	}
    	player.setScoreboard(board);
    }
    
   /* @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
    	Bukkit.broadcastMessage(ChatColor.RED+"aaa");
    	if(start==1)
    	{
		    //String killed = e.getEntity().getName();
    		Bukkit.broadcastMessage(ChatColor.RED+"aaa");
		    Player killer = e.getEntity().getKiller();
		    for (Player playerz : Bukkit.getOnlinePlayers()) {
		    	setsb(playerz,killer,1);
		    	}

		    
		   // e.setDeathMessage(ChatColor.RED + killed + " has been slain by " + killer);
    	}

    }*/
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
	if(start==1)
	{

	    //String killed = e.getEntity().getName();
		if(!(e.getEntity().getKiller() instanceof Player))
		{
		Player dead = e.getEntity().getPlayer();
	    Location loc = dead.getLocation();
	    dead.setHealth(20);
	    dead.teleport(loc);
	    dead.setGameMode(GameMode.SPECTATOR);
	    
	    for (Player playerz : Bukkit.getOnlinePlayers()) {
	    	updatesb(playerz,dead);
	    	//Bukkit.broadcastMessage(ChatColor.RED+"aaa");
	    	}
	    checkwinner(dead);
			return;
		}
		Player killer=e.getEntity().getKiller();
		int amount = killmap.get(killer.getDisplayName());
		amount++;
		killmap.put(killer.getDisplayName(), amount);
	    Player dead = e.getEntity().getPlayer();
	    Location loc = dead.getLocation();
	    dead.setHealth(20);
	    dead.teleport(loc);
	    dead.setGameMode(GameMode.SPECTATOR);
	    for (Player playerz : Bukkit.getOnlinePlayers()) 
	    {
	    	updatesb(playerz,dead);
	    	//Bukkit.broadcastMessage(ChatColor.RED+"aaa");
	    }
	    checkwinner(dead);

	    
	   // e.setDeathMessage(ChatColor.RED + killed + " has been slain by " + killer);
	}
	}
    public void updatesb(Player player,Player dead) {

    	ScoreboardManager manager = Bukkit.getScoreboardManager();
    	Scoreboard board = manager.getNewScoreboard();
    	Objective objective = board.registerNewObjective("test", "dummy");
    	objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    	objective.setDisplayName(ChatColor.RED +"战   地");
    	Score info = objective.getScore(ChatColor.GREEN+"-----游戏规则-----");
    	info.setScore(999);
    	Score info2 = objective.getScore(ChatColor.LIGHT_PURPLE+"刺激的吃鸡游戏");
    	info2.setScore(998);
    	Score info3 = objective.getScore(ChatColor.LIGHT_PURPLE+"安全圈不断缩小");
    	info3.setScore(997);
    	Score info4 = objective.getScore(ChatColor.LIGHT_PURPLE+"击杀所有敌人！");
    	info4.setScore(996);
    	Score killt = objective.getScore(ChatColor.GREEN+"-----存活列表-----");
    	killt.setScore(995);
    	for (Player playerz : Bukkit.getOnlinePlayers()) {
    		if(playerz!=dead && playerz.getGameMode()!=GameMode.SPECTATOR){
    		objective.getScore(ChatColor.DARK_RED + playerz.getDisplayName()).setScore(killmap.get(playerz.getDisplayName()));}
    	}
    	player.setScoreboard(board);
    }
    
   /* public void showhealth() {
    	ScoreboardManager managers = Bukkit.getScoreboardManager();
    	Scoreboard boards = managers.getNewScoreboard();
    	 
    	Objective objective = boards.registerNewObjective("showhealth", "health");
    	objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    	objective.setDisplayName("/ 20"+ChatColor.RED+"  ♥");
    	 
    	for(Player online : Bukkit.getOnlinePlayers()){
    	  online.setScoreboard(boards);
    	  online.setHealth(online.getHealth()); //Update their health
    	}
    }*/
    
  /* @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
    	if (e.getEntity() instanceof  Player && e.getDamager() instanceof Player) {
    		showhealth();
    	}
    }*/
  /*  @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
    if (e.getEntity().getKiller() == null) {
    return;
    }
    Bukkit.broadcastMessage(ChatColor.RED+"aaa");
	if(start==1)
	{
	    //String killed = e.getEntity().getName();
		Bukkit.broadcastMessage(ChatColor.RED+"aaa");
	    Player killer = e.getEntity().getKiller();
	    for (Player playerz : Bukkit.getOnlinePlayers()) {
	    	setsb(playerz,killer,1);
	    	}

	    
	   // e.setDeathMessage(ChatColor.RED + killed + " has been slain by " + killer);
	}
    //Do your thing
    }*/
 /*   @EventHandler
    public void onPlayerHitFishingRodEventThingyName (final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            final Player caught = (Player) event.getCaught();
            if (player.getItemInHand().getType() == Material.FISHING_ROD) {
                player.sendMessage(""+caught.getHealth());
            }
        }
    }
    */
    @EventHandler
    public void hookhit(EntityDamageByEntityEvent event) {
    	
    
    	if(event.getDamager() instanceof FishHook && event.getEntity() instanceof Player) {
    		java.util.List<Entity> nearby = event.getDamager().getNearbyEntities(50,50,50);
    		for (Entity e : nearby) {
    			if(e instanceof Player && e != event.getEntity())
    			{
    				e.sendMessage(((Player)event.getEntity()).getDisplayName()+"还有 "+((Player)event.getEntity()).getHealth()+" 血量");
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event)
    {
    	if(allowbreakopendie==0) {event.setCancelled(true);}
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent  e) 
    {
    	if(allowbreakopendie==0) {
    			e.setCancelled(true);
    	}
    }
    
    
    @EventHandler(priority=EventPriority.HIGH)
	public void onInventoryOpenEvent(InventoryOpenEvent e)
    {
    	if(allowbreakopendie==0 || start==0) {e.getPlayer().sendMessage("比赛尚未开始！");e.setCancelled(true);}
    	
    }
    
    
    public void createopstuff(Player player) {
    	//Bukkit.broadcastMessage(ChatColor.RED+"aaa2");
    	Random rand = new Random();
    	if(endermap.get(player.getDisplayName())==0) {
    		player.getEnderChest().clear();
    		int which = rand.nextInt(100);
    		if(which<=25) 
    		{
    		ItemStack item = new ItemStack(Material.BOW);
    		ItemMeta meta = item.getItemMeta();
    		meta.addEnchant(Enchantment.ARROW_INFINITE, 10,true);
    		ArrayList<String> lore = new ArrayList<String>();
    		lore.add("TNTBOW");
    		lore.add("消耗TNT发出爆炸箭！");
    		lore.add("若无法使用请先将包中TNT扔下再捡起!");
    		meta.setLore(lore);
    		meta.setDisplayName(ChatColor.DARK_RED+"TNT神弓");
    		item.setItemMeta(meta);
    		//player.getEnderChest().setItem(rand.nextInt(27),new ItemStack(Material.TNT,rand.nextInt(20)));
    		if(player.getInventory().contains(Material.TNT)) {player.sendMessage(ChatColor.GOLD+"请先丢弃背包中的TNT！");return;}
    		player.getEnderChest().setItem(rand.nextInt(27),item);
    		player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.TNT,rand.nextInt(20)));
    		player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW,1));
    		for (Player playerz : Bukkit.getOnlinePlayers()) {
				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN +player.getDisplayName()+ ChatColor.BLUE + " 已获得神器： " + ChatColor.
GOLD + "TNT弓！ "+"\"}");
				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
			}
    		}
    		else if(which<=50)
    		{
    			startTimes.put(player.getDisplayName(), System.currentTimeMillis());
    			ItemStack item = new ItemStack(Material.COMPASS);
        		ItemMeta meta = item.getItemMeta();
        		meta.addEnchant(Enchantment.FIRE_ASPECT, 5,true);
        		ArrayList<String> lore = new ArrayList<String>();
        		lore.add("teleportcompass");
        		lore.add("右键传送到周围100格内随机玩家身上！");
        		lore.add("冷却5秒！");
        		meta.setLore(lore);
        		meta.setDisplayName(ChatColor.DARK_RED+"传送指南针");
        		item.setItemMeta(meta);
        		player.getEnderChest().setItem(rand.nextInt(27),item);
        		for (Player playerz : Bukkit.getOnlinePlayers()) {
    				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN +player.getDisplayName()+ ChatColor.BLUE + " 已获得神器： " + ChatColor.
    GOLD + "传送指南针！ "+"\"}");
    				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
    			}
    		}
    		else if(which<=75)
    		{

    			ItemStack item = new ItemStack(Material.BONE);
    			ItemMeta meta = item.getItemMeta();
    			meta.addEnchant(Enchantment.KNOCKBACK, 10,true);
    			ArrayList<String> lore = new ArrayList<String>();
    			lore.add("kbbone");
    			lore.add("击退敌人！");
    			lore.add("拿着它你速度飞快！");
    			meta.setLore(lore);
    			meta.setDisplayName(ChatColor.DARK_RED+"推推棒");
    			item.setItemMeta(meta);
    			player.getEnderChest().setItem(rand.nextInt(27),item);
    			for (Player playerz : Bukkit.getOnlinePlayers()) {
    				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN +player.getDisplayName()+ ChatColor.BLUE + " 已获得神器： " + ChatColor.
    GOLD + "推推棒！ "+"\"}");
    				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
    			}
    		}
    		else if(which<100)
    		{
    			lightstartTimes.put(player.getDisplayName(), System.currentTimeMillis());
    			ItemStack item = new ItemStack(Material.CARROT_STICK);
    			ItemMeta meta = item.getItemMeta();
    			meta.addEnchant(Enchantment.DURABILITY, 10,true);
    			ArrayList<String> lore = new ArrayList<String>();
    			lore.add("lightingstick");
    			lore.add("掌控雷电的力量!");
    			lore.add("召唤天雷攻击周围生物!");
    			meta.setLore(lore);
    			meta.setDisplayName(ChatColor.DARK_RED+"雷电法杖");
    			item.setItemMeta(meta);
    			player.getEnderChest().setItem(rand.nextInt(27),item);
    			for (Player playerz : Bukkit.getOnlinePlayers()) {
    				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN +player.getDisplayName()+ ChatColor.BLUE + " 已获得神器： " + ChatColor.
    GOLD + "雷电法杖！ "+"\"}");
    				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(title);
    				((CraftPlayer) playerz).getHandle().playerConnection.sendPacket(length);
    			}
    		}
    		
    		endermap.put(player.getDisplayName(), 1);
    	}else
    	{
    		player.sendMessage(ChatColor.GOLD+"你已经领取过一件神器！");
    	}

    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onProjectileHit2(ProjectileHitEvent event) {
    	Projectile projectile = event.getEntity();

    	if(!(projectile instanceof Arrow)) { /* do nothing */ }
        else {
        	try {
        	Arrow arrowProj = (Arrow) projectile;
        	Projectile p = event.getEntity();
        	Player player = (Player) arrowProj.getShooter();
        	World world = arrowProj.getWorld();
        	if(player.getItemInHand().getItemMeta().getLore().contains("TNTBOW"))
        	{
        		//Bukkit.broadcastMessage(ChatColor.RED+"its a tnt bow");
        		player.updateInventory();
        		if ((!player.getInventory().contains(Material.TNT))) {
                    player.sendMessage("没有足够tnt!");}
        		else
        		{
        			for (ItemStack item : player.getInventory().getContents()) 
        			{
        				if (item.getType() == Material.TNT) 
        				{
        					if(item.getAmount()==1) {
        						player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
        						//player.updateInventory();
        					}
        					item.setAmount(item.getAmount() - 1);
        					//player.getInventory().updateInventory();
        					break;
        				}
        			}
        			
        			Location boom = event.getEntity().getLocation();
        			p.remove();
        			world.createExplosion(boom, 4);
        			
        		}
        
        	}}catch(Exception ex) {}
}
}
    
    
   /* @EventHandler(priority=EventPriority.HIGH)
	public void onEnderchestopen(InventoryOpenEvent e){
    	if (e.getInventory().getHolder() instanceof EnderChest ){
    		Bukkit.broadcastMessage(ChatColor.RED+"aaa");
    		createopstuff((Player)e.getPlayer());
    	}
    	
    }*/
    /*@SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	 
       
        //Block block = (Block) event.getClickedBlock();
        if(event.getClickedBlock().getTypeId() == 130)
        {
        	Player p = event.getPlayer();
        	createopstuff(p);
        	Bukkit.broadcastMessage(ChatColor.RED+"aaa");
        }
        else {
        	return;
        }
    }*/
    
	@EventHandler
	public void onInventoryOpenEventz(InventoryOpenEvent e){
		try 
		{
    	if (InventoryType.ENDER_CHEST.equals(e.getInventory().getType()))
    	{
    		//Bukkit.broadcastMessage(ChatColor.RED+"in");
    		createopstuff((Player)e.getPlayer());
    	}
		}
		catch(Exception ex)
		{
			e.getPlayer().sendMessage("比赛尚未开始！");
		}
    }

	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		if(start==1) {
        Action a = event.getAction();
        ItemStack is = event.getItem();
 
        if(a == Action.PHYSICAL || is == null || is.getType()== Material.AIR)
        { return;}
 
        if(is.getType() == Material.COMPASS)
        {
        	try {
        	if(is.getItemMeta().getLore().contains("teleportcompass"))
        	{
        		long startz = startTimes.get(event.getPlayer().getDisplayName()),
        		end = startz + 5000;
        		if(end < System.currentTimeMillis()) { // means if current time is after the end
        			int iftel = 0;
        			java.util.List<Entity> nearby = event.getPlayer().getNearbyEntities(100,100,100);
            		for (Entity e : nearby) {
            			
            			if(e instanceof Player && e != event.getPlayer())
            			{
            				event.getPlayer().sendMessage(ChatColor.GOLD+"你传送到了"+ChatColor.GREEN+e.getName()+ChatColor.GOLD+"的身上!");
            				Location tpt = e.getLocation();
            				event.getPlayer().teleport(tpt);
            				iftel=1;
            				break;
            			}
            		}
        			
            		if(iftel==1)
        		    {startTimes.put(event.getPlayer().getDisplayName(), System.currentTimeMillis());}
            		else {
            			event.getPlayer().sendMessage("你的周围没有玩家!");
            		}
        		} else {
        		    long timeToWait = end - System.currentTimeMillis(); // in ms
        		    int ttw = (int)(timeToWait/1000)+1;
        		    event.getPlayer().sendMessage(ChatColor.GOLD+"传送冷却还有 "+ChatColor.GREEN+ttw+ChatColor.GOLD+" 秒");
        		    // print time to wait
        		}
        		}
        	}
        	catch(Exception e)
        	{
        			event.getPlayer().sendMessage("这不是你的指南针！");
        	}
        }
        
        
        if(is.getType() == Material.CARROT_STICK)
        {
        	try 
        	{
        	if(is.getItemMeta().getLore().contains("lightingstick"))
        	{
        		long startz = lightstartTimes.get(event.getPlayer().getDisplayName()),
                end = startz + 5000;
        		if(end < System.currentTimeMillis())
        		{
        			java.util.List<Entity> nearby = event.getPlayer().getNearbyEntities(50,50,50);
        			for (Entity e : nearby) {
        				if(e != event.getPlayer()) {
        				event.getPlayer().getWorld().strikeLightning(e.getLocation());
        				e.setFireTicks(400);
        				e.setVelocity(new Vector(0,1.5,0));
        				}
        			}
        			event.getPlayer().sendMessage(ChatColor.GOLD+"你召唤了雷电! ");
        			lightstartTimes.put(event.getPlayer().getDisplayName(), System.currentTimeMillis());
        			
        		}
        		else 
        		{
        			long timeToWait = end - System.currentTimeMillis(); // in ms
        		    int ttw = (int)(timeToWait/1000)+1;
        		    event.getPlayer().sendMessage(ChatColor.GOLD+"雷击冷却还有 "+ChatColor.GREEN+ttw+ChatColor.GOLD+" 秒");
        		}
        	}
        	}
        	catch(Exception e)
        	{
        			event.getPlayer().sendMessage("这不是你的雷电法杖！");
        	}
        }
		}
}
	
	
	public void checkwinner(Player dead)
	{
		int survivalp = 0;
		for (Player playerz : Bukkit.getOnlinePlayers()) 
		    {
		    	if(playerz.getGameMode() == GameMode.SURVIVAL)
		    	{
		    		survivalp++;
		    	}
		    	//Bukkit.broadcastMessage(ChatColor.RED+"aaa");
		    }
		//Bukkit.broadcastMessage(ChatColor.RED+""+survivalp);
		//Bukkit.broadcastMessage(ChatColor.RED+"aaa");
		if(survivalp==1) 
		{
			killmap.clear();
			endermap.clear();
			ingame.clear();
			stopbdTimer();
			start = 0;
	    	for (Player playerz : Bukkit.getOnlinePlayers()) 
		    {
	    		//Bukkit.broadcastMessage(ChatColor.RED+"aaa");
	    		//Bukkit.broadcastMessage(ChatColor.RED+"aaa2");
	    		if(playerz.getGameMode() == GameMode.SURVIVAL)
	    		{
	    			//Bukkit.broadcastMessage(ChatColor.RED+"aaa2");
	    			for (Player players : Bukkit.getOnlinePlayers()) 
	    			{
    					IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + "本场游戏结束！胜利者为： "+ChatColor.GREEN+playerz.getDisplayName()+"\"}");
    					PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
    					PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
    					((CraftPlayer) players).getHandle().playerConnection.sendPacket(title);
    					((CraftPlayer) players).getHandle().playerConnection.sendPacket(length);
    					players.setGameMode(GameMode.CREATIVE);
    					players.teleport(playerz.getLocation());
    					players.sendMessage(ChatColor.GOLD + "本场游戏结束！胜利者为： "+ChatColor.GREEN+playerz.getDisplayName());
    				}
	    			
	    	    	FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true).with(Type.BALL).withColor(Color.ORANGE).withFade(Color.RED).build();
	    	    	FireworkEffect fe2 = FireworkEffect.builder().flicker(false).trail(true).with(Type.BURST).withColor(Color.RED).withFade(Color.BLUE).build();
	    	    	FireworkEffect fe3 = FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.SILVER).withFade(Color.PURPLE).build();
	    	    	final int fw = getServer().getScheduler().scheduleSyncDelayedTask(this, (Runnable) new BukkitRunnable() {
	    	            int count = 20;
	    	                public void run(){
	    	                	Location location = playerz.getLocation();
	    	                	new InstantFirework(fireworkEffect, location);
	    	                	new InstantFirework(fe2, location);
	    	                	new InstantFirework(fe3, location);
	    	                    count--;
	    	                    if (count == 0) {
	    	                        this.cancel();
	    	                    }
	    	                }
	    	        }.runTaskTimer(this, 0L, 20L));
	    	    	break;
	    		}
	    		
		    }
		}
	    else if(survivalp==0)
	    {
			killmap.clear();
			endermap.clear();
			ingame.clear();
			stopbdTimer();
			start = 0;
	    	for (Player players : Bukkit.getOnlinePlayers()) {
				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + "本场游戏结束！胜利者为： "+ChatColor.GREEN+dead.getDisplayName()+"\"}");
				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 50, 5);
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(length);
				players.setGameMode(GameMode.CREATIVE);
				players.sendMessage(ChatColor.GOLD + "本场游戏结束！胜利者为： "+ChatColor.GREEN+dead.getDisplayName());

			}
	    	FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true).with(Type.BALL).withColor(Color.ORANGE).withFade(Color.RED).build();
	    	FireworkEffect fe2 = FireworkEffect.builder().flicker(false).trail(true).with(Type.BURST).withColor(Color.RED).withFade(Color.BLUE).build();
	    	FireworkEffect fe3 = FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.SILVER).withFade(Color.PURPLE).build();
	    	final int fw = getServer().getScheduler().scheduleSyncDelayedTask(this, (Runnable) new BukkitRunnable() {
	            int count = 20;
	                public void run(){
	                	Location location = dead.getLocation();
	                	new InstantFirework(fireworkEffect, location);
	                	new InstantFirework(fe2, location);
	                	new InstantFirework(fe3, location);
	                    count--;
	                    if (count == 0) {
	                        this.cancel();
	                    }
	                }
	        }.runTaskTimer(this, 0L, 20L));


	    }
}
	
	
	@EventHandler
	public void onswitch(PlayerItemHeldEvent event)
	{
		if(start == 1) 
		{
			int onbone = 0;
			
			try {
				ItemStack is = event.getPlayer().getInventory().getItem(event.getNewSlot());
				if(is.getItemMeta().getLore().contains("kbbone"))
				{onbone=1;}
				}
			catch(Exception ex)
			{onbone = 0;}
			
			//Bukkit.broadcastMessage(ChatColor.RED+is.getItemMeta().getDisplayName());
			if(onbone == 1)
			{
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 10, true, false));
		    	/*final int fw = getServer().getScheduler().scheduleSyncDelayedTask(this, (Runnable) new BukkitRunnable() {
		            int count = 5;
		                public void run(){
		                	
		                	count--;
		                    if (count == 0) {
		                        this.cancel();
		                    }
		                }
		        }.runTaskTimer(this, 0L, 5L));*/
			}
			else
			{
				final int fw = getServer().getScheduler().scheduleSyncDelayedTask(this, (Runnable) new BukkitRunnable() {
		            int count = 5;
		                public void run(){
		                	count--;
		                    if (count == 0) {
		                    	//event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 0, 0), true);
		                    	event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
		                        this.cancel();
		                    }
		                }
		        }.runTaskTimer(this, 0L, 20L));
				//Bukkit.broadcastMessage(ChatColor.RED+"aaa2");
				
			}
		}

	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		if(start == 1)
		{
			
			try 
			{
				if(ingame.get(event.getPlayer().getDisplayName())!=1)
				{
					p.setGameMode(GameMode.SPECTATOR);
					p.sendMessage(ChatColor.DARK_RED+"游戏正在进行中！请观战！");
					return;
				}
			}
			catch(Exception ex)
			{
				p.setGameMode(GameMode.SPECTATOR);
				p.sendMessage(ChatColor.DARK_RED+"游戏正在进行中！请观战！");
				return;
			}
			p.sendMessage(ChatColor.GREEN+"欢迎回来！系统记录到你正在进行游戏！");
			
		}
		else
		{
			p.sendMessage(ChatColor.GREEN+"欢迎来到本服务器！当前空闲状态！");
		}
	}
}
