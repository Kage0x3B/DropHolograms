package de.syscy.dropholograms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class DropHologramsPlugin extends JavaPlugin implements Listener {
	private String itemInfoFormat;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		itemInfoFormat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("itemInfoFormat", "&6%amount% %name%"));

		getServer().getPluginManager().registerEvents(this, this);

		for(World world : Bukkit.getWorlds()) {
			for(Item itemEntity : world.getEntitiesByClass(Item.class)) {
				if(itemEntity != null) {
					updateDisplayName(itemEntity);
				}
			}
		}
	}

	@Override
	public void onDisable() {
		for(World world : Bukkit.getWorlds()) {
			for(Item itemEntity : world.getEntitiesByClass(Item.class)) {
				if(itemEntity != null) {
					itemEntity.setCustomName("");
					itemEntity.setCustomNameVisible(false);
				}
			}
		}
	}

	private void updateDisplayName(Item itemEntity) {
		updateDisplayName(itemEntity, -1);
	}

	private void updateDisplayName(Item itemEntity, int overrideAmount) {
		ItemStack itemStack = itemEntity.getItemStack();

		if(itemStack == null || itemStack.getType() == Material.AIR) {
			return;
		}

		String itemName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : improveStringLook(itemStack.getType().name());
		int amount = overrideAmount >= 0 ? overrideAmount : itemStack.getAmount();

		String itemCustomName = itemInfoFormat.replace("%amount%", String.valueOf(amount));
		itemCustomName = itemCustomName.replace("%name%", itemName);

		itemEntity.setCustomName(itemCustomName);
		itemEntity.setCustomNameVisible(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDrop(PlayerDropItemEvent event) {
		updateDisplayName(event.getItemDrop());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemMerge(ItemMergeEvent event) {
		int newAmount = event.getTarget().getItemStack().getAmount() + event.getEntity().getItemStack().getAmount();

		updateDisplayName(event.getTarget(), newAmount);
	}

	private String improveStringLook(String string) {
		string = string.replaceAll("_", " ");

		String[] stringSplit = string.split(" ");
		StringBuilder stringBuilder = new StringBuilder(string.length());

		for(int i = 0; i < stringSplit.length; i++) {
			String part = stringSplit[i];

			stringBuilder.append(part.substring(0, 1).toUpperCase()).append(part.substring(1, part.length()).toLowerCase()).append(i == stringSplit.length - 1 ? "" : " ");
		}

		return stringBuilder.toString();
	}
}