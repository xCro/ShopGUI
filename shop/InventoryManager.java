package shop;

/*
 * Shop Plugin made by xRo
 * Any person(s) using this code are subject to
 * a $1000 fine if used without permissions.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryManager {
	
	private List<List<ShopItem>> pages = new ArrayList<List<ShopItem>>();
	private Main main;
	
	InventoryManager(Main main) {
		this.main = main;
	}
	
	public void inventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player pl = (Player) e.getWhoClicked();
		Integer slot = e.getSlot();
		if (inv.getHolder() == null && inv.getSize() == 54 && inv.getName().equals(main.getShopName())) {
			e.setCancelled(true);
			ItemStack pageItem = inv.getItem(49);
			if (pageItem != null && pageItem.getItemMeta() != null && pageItem.getItemMeta().hasLore()) {
				ItemMeta pageMeta = pageItem.getItemMeta();
				String pageString = ChatColor.stripColor(pageMeta.getLore().get(0)).replace("Page ", "");
				if (main.tryParseInt(pageString)) {
					Integer pageInteger = Integer.parseInt(pageString) - 1; // Dehumanise (array indexes start at 0, not 1. Error 404, page 0 does not exist)
					if (slot < 36) {
						ShopItem shopItem = pages.get(pageInteger).get(slot);
						if (shopItem.getCost() > main.getCoins(pl)) {
							main.modifyCoins(pl, 1000);
							openShop(pl, pageInteger);
							main.actionBar(pl, "§cYou don't have enough coins to buy this item.");
							pl.sendMessage("§cYou don't have enough coins to buy this item.");
							pl.sendMessage("§bBut why not take these 1000 coins?");
							pl.sendMessage("§bYou don't have a choice. You now have 1000 more coins.");
						} else {
							PlayerInventory playerInv = pl.getInventory();
							if (playerInv.firstEmpty() < 0) {
								main.actionBar(pl, "§cYour inventory is full! Purchase cancelled.");
								pl.sendMessage("§cYour inventory is full! Purchase cancelled.");
							} else {
								main.modifyCoins(pl, -shopItem.getCost());
								playerInv.addItem(shopItem.getItemStack());
								openShop(pl, pageInteger);
								main.actionBar(pl, "§bYou bought §e" + shopItem.getName() + " for §6" + shopItem.getCost() + " coins§b.");
								pl.sendMessage("§bYou bought §e" + shopItem.getName() + " for §6" + shopItem.getCost() + " coins§b.");
							}
						}
					} else if (inv.getItem(slot) != null) {
						if (slot == 48) openShop(pl, pageInteger - 1);
						else if (slot == 50) openShop(pl, pageInteger + 1);
					}
				} else {
					main.actionBar(pl, "§cAn error occurred!");
					pl.sendMessage("§cAn error occurred!");
				}
			}
		}
	}
	
	public void openShop(Player pl, int pageNumber) {
		Inventory inv = Bukkit.createInventory(null, 54, "Shop");
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemStack emerald = new ItemStack(Material.EMERALD);
		ItemMeta meta = arrow.getItemMeta();
		if (pageNumber >= 1) {
			meta.setDisplayName("§aPage " + pageNumber);
			arrow.setItemMeta(meta);
			inv.setItem(48, arrow);
		}
		if (pages.size() > pageNumber + 1) {
			meta.setDisplayName("§aPage " + (pageNumber + 2));
			arrow.setItemMeta(meta);
			inv.setItem(50, arrow);
		}
		meta.setDisplayName("§bWelcome to the Shop!");
		meta.setLore(Arrays.asList("§7Page §a" + (pageNumber + 1), "§7Use the arrow buttons to navigate through pages", "§7Click an item to buy it in exchange for coins", "", "§7Your balance: §6" + main.getCoins(pl)));
		emerald.setItemMeta(meta);
		inv.setItem(49, emerald);
		int loc = 0;
		Integer coins = main.getCoins(pl);
		if (!(pages.size() <= pageNumber)) {
			List<ShopItem> page = pages.get(pageNumber);
			for (ShopItem si : page) {
				ItemStack item = si.getItemStack().clone();
				ItemMeta siMeta = item.getItemMeta();
				siMeta.setLore(Arrays.asList("§7Cost: §6" + si.getCost(), "", (coins < si.getCost() ? "§cYou don't have enough coins!" : "§eClick to purchase this item")));
				item.setItemMeta(siMeta);
				inv.setItem(loc, item);
    			loc++;
			}
		}
    	pl.openInventory(inv);
	}
	
	void addItem(ShopItem si) {
		Boolean hasPage = false;
		for (List<ShopItem> page : pages) {
			if (page.size() < 36) {
				page.add(si);
				hasPage = true;
				break;
			} else continue;
		}
		if (!hasPage) {
			List<ShopItem> newPage = new ArrayList<ShopItem>();
			newPage.add(si);
			pages.add(newPage);
		}
	}
}