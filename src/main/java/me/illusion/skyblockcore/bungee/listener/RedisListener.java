package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RedisListener extends BinaryJedisPubSub implements PacketProcessor {

    private static final byte[] KEY = "SkyblockChannel".getBytes(StandardCharsets.UTF_8);

    private final SkyblockBungeePlugin main;
    private final Jedis jedis;

    public RedisListener(SkyblockBungeePlugin main) {
        this.main = main;
        this.jedis = main.getJedisUtil().getJedis();
        jedis.subscribe(this, KEY);
    }

    public void updatePlayer(UUID uuid, String proxy) {
        jedis.set(uuid.toString().getBytes(StandardCharsets.UTF_8), proxy.getBytes(StandardCharsets.UTF_8));
    }

    public String getProxy(UUID uuid) {
        byte[] bytes = jedis.get(uuid.toString().getBytes(StandardCharsets.UTF_8));
        return new String(bytes);
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if (channel != KEY)
            return;

        main.getPacketManager().read(message);
    }

    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {

    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {

    }

    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {

    }

    @Override
    public void send(Packet packet) {
        jedis.publish(KEY, packet.getAllBytes());
    }
}
