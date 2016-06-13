package shop;

/*
 * Shop Plugin made by xCro
 * Any person(s) using this code are subject to
 * a $1000 fine if used without permissions.
 */

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class FakePlayer extends EntityPlayer {
	
	public FakePlayer(MinecraftServer arg0, WorldServer arg1, GameProfile arg2, PlayerInteractManager manager) {
		super(arg0, arg1, arg2, new PlayerInteractManager(arg1));
		playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), this);
		this.playerInteractManager.setGameMode(EnumGamemode.SURVIVAL);
	}
	
	public static void spawnShop(Location l) {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.getWorldServer(0);
		UUID uuid = UUID.randomUUID();
		GameProfile profile = new GameProfile(uuid, "NPC-01");
		PlayerInteractManager manager = new PlayerInteractManager(world);
		FakePlayer npc = new FakePlayer(server, world, profile, manager);
		npc.playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), npc);
		npc.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.addEntity(npc, SpawnReason.NATURAL);
	}
	
}