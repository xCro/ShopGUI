package shop;

/*
 * Shop Plugin made by xCro
 * Any person(s) using this code are subject to
 * a $1000 fine if used without permissions.
 */

import java.util.UUID;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

public class NPCHuman {
	
	private EntityPlayer shop;
	
	public NPCHuman(Location loc, String name, UUID uuid) {
		/* *************************** */
		/* Random online player's skin */
		for (Player pl : Bukkit.getOnlinePlayers())
			uuid = pl.getUniqueId();
		/* *************************** */
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		shop = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(uuid, name), new PlayerInteractManager(nmsWorld));
		shop.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
	
	public Location getLocation() {
		return shop.getBukkitEntity().getLocation();
	}
	
	public void send() {
		DataWatcher d = new DataWatcher(null);
		d.a(0, (Object) (byte) 0);
		d.a(1, (Object) (short) 0);
		d.a(8, (Object) (byte) 0);
		for (Player pl : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) pl).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, shop));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(shop));
			//connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, shop));
		}
	}
	
	public void send(Player pl) {
		DataWatcher d = new DataWatcher(null);
		d.a(0, (Object) (byte) 0);
		d.a(1, (Object) (short) 0);
		d.a(8, (Object) (byte) 0);
		PlayerConnection connection = ((CraftPlayer) pl).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, shop));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(shop));
		//connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, shop));
	}
	
	public void teleport(Location loc) {
		remove();
		shop.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		send();
	}
	
	public void remove() {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(shop.getId());
        for (Player pl : Bukkit.getOnlinePlayers())
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
	}
}