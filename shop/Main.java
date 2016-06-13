package shop;

/*
 * Shop Plugin made by xCro
 * Any person(s) using this code are subject to
 * a $1000 fine if used without permissions.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public Map<UUID, Integer> coins = new HashMap<UUID, Integer>();
	private FileConfiguration config;
	private Main instance;
	private InventoryManager inventoryManager;
	private NPCHuman shop;
	
	public void onEnable() {
		config = getConfig();
		if (!config.isConfigurationSection("items")) {
			config.set("items.&bDiamond.cost", 300);
			config.set("items.&bDiamond.material", "DIAMOND");
			config.set("items.&bDiamond.amount", 10);
			config.set("items.&bDiamond.data", 0);
			saveConfig();
		}
		instance = this;
		inventoryManager = new InventoryManager(this);
		ShopItem.loadItems(inventoryManager, config);
		shop();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	void playerJoin(PlayerJoinEvent e) {
		shop.send(e.getPlayer());
	}
	
	@EventHandler
	void inventoryClick(InventoryClickEvent e) {
		inventoryManager.inventoryClick(e);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You cannot use this command!");
			return true;
		}
		Player pl = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("shop"))
			inventoryManager.openShop(pl, 0);
		else if (cmd.getName().equalsIgnoreCase("spawn"))
			shop.teleport(pl.getLocation());
		return true;
	}
	
	public String getShopName() {
		if (!config.isString("inventoryName"))
			config.set("inventoryName", "Shop");
		return config.getString("inventoryName");
	}
	
	public Integer getCoins(Player pl) {
		UUID uuid = pl.getUniqueId();
		if (coins.containsKey(uuid))
			return coins.get(uuid);
		else coins.put(uuid, 0);
		return 0;
	}
	
	public void setCoins(Player pl, int coins) {
		this.coins.put(pl.getUniqueId(), coins);
		instance.save();
	}
	
	public void modifyCoins(Player pl, int coins) {
		UUID uuid = pl.getUniqueId();
		if (this.coins.containsKey(uuid))
			this.coins.put(uuid, this.coins.get(uuid) + coins);
		else this.coins.put(uuid, coins);
		instance.save();
	}
	
	public void save() {
		for (Entry<UUID, Integer> coinData : coins.entrySet())
			config.set("coins." + coinData.getKey(), coinData.getValue());
		saveConfig();
	}
	
	public void actionBar(Player pl, String message) {
		PacketPlayOutChat msg = new PacketPlayOutChat(ChatSerializer.a("{text:\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) pl).getHandle().playerConnection.sendPacket(msg);
	}
	
	public boolean tryParseInt(String value) {  
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void shop() {
		World world = Bukkit.getWorlds().get(0);
		shop = new NPCHuman(new Location(world, 0, 0, 0), "Shop", UUID.randomUUID());
		shop.send();
	}
}