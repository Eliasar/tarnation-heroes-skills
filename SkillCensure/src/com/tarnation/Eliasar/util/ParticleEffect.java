package com.tarnation.Eliasar.util;

import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ParticleEffect {

    private String name;
    private Location loc;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float power;
    private int amount;

    public ParticleEffect(String name, Location loc, float power, int amount) {
        this.name = name;
        this.loc = loc;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.power = power;
        this.amount = amount;
    }

    public ParticleEffect(String name, Location loc, float offsetX, float offsetY, float offsetZ, float power, int amount) {
        this.name = name;
        this.loc = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.power = power;
        this.amount = amount;
    }

    public void playEffect() {
        PacketPlayOutWorldParticles particles =
                new PacketPlayOutWorldParticles (
                        name,
                        (float) loc.getX(),
                        (float) loc.getY(),
                        (float) loc.getZ(),
                        offsetX, offsetY, offsetZ,
                        power,
                        amount
                );
        try {
            sendPacketToLocation(loc, particles);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static void sendPacketToLocation(Location l, PacketPlayOutWorldParticles packet)
            throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException {
        for (Entity e : getNearbyEntities(l, 14)) { // In this case I used a radius of 20
            if (e instanceof Player) {
                Player p = (Player) e;
                try {
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static List<Entity> getNearbyEntities(Location l, int range) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Entity entity : l.getWorld().getEntities()) {
            if (isInBorder(l, entity.getLocation(), range)) {
                entities.add(entity);
            }
        }
        return entities.isEmpty() ? null : entities;
    }

    private static boolean isInBorder(Location center, Location l, int range) {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = l.getBlockX(), z1 = l.getBlockZ();
        return !(x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range));
    }
}