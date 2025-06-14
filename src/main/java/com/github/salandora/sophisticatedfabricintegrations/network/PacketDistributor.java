package com.github.salandora.sophisticatedfabricintegrations.network;

import com.github.salandora.sophisticatedfabricintegrations.init.ModPayloads;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PacketDistributor {
	private PacketDistributor() {
	}

	public static <T extends C2SPacket> void sendToServer(T packet) {
		ModPayloads.getChannel().sendToServer(packet);
	}

	public static <T extends S2CPacket> void sendToAllNear(T packet, Entity entity, double range) {
		for (ServerPlayer player : PlayerLookup.around((ServerLevel) entity.level(), entity.position(), range)) {
			ModPayloads.getChannel().sendToClient(packet, player);
		}
	}
	public static <T extends S2CPacket> void sendToAllNear(T message, Level level, Vec3 pos, int range) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		sendToAllNear(message, serverLevel, pos, range);
	}
	public static <T extends S2CPacket> void sendToAllNear(T message, ServerLevel level, Vec3 pos, int range) {
		for (ServerPlayer player : PlayerLookup.around(level, pos, range)) {
			ModPayloads.getChannel().sendToClient(message, player);
		}
	}

	public static <T extends S2CPacket> void sendToPlayer(Player player, T packet) {
		if (player instanceof ServerPlayer serverPlayer) {
			ModPayloads.getChannel().sendToClient(packet, serverPlayer);
		}
	}
	public static <T extends S2CPacket> void sendToPlayer(ServerPlayer player, T packet) {
		ModPayloads.getChannel().sendToClient(packet, player);
	}
}
