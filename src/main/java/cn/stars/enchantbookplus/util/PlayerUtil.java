package cn.stars.enchantbookplus.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtil {
    public static void setInstantBuild(Player player, boolean instantBuild) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ABILITIES);
        packet.getBooleans().write(0, player.isInvulnerable());
        packet.getBooleans().write(1, player.isFlying());
        packet.getBooleans().write(2, player.getAllowFlight());
        packet.getBooleans().write(3, instantBuild);
        packet.getFloat().write(0, player.getFlySpeed() / 2);
        packet.getFloat().write(1, player.getWalkSpeed() / 2);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

}
