package me.zeroX150.atomic.feature.module.impl.testing;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TestModule extends Module {
    public TestModule() {
        super("Test", "The dupe has been moved over to Dupe:.d 2 btw", ModuleType.HIDDEN);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) return;
            Packet<?> packet = ((PacketEvent) event).getPacket();
            if (packet.getClass().getSimpleName().toLowerCase().contains("entity") || packet.getClass() == WorldTimeUpdateS2CPacket.class)
                return;
            //OpenWrittenBookS2CPacket p = (OpenWrittenBookS2CPacket) packet;
            Utils.Client.sendMessage(packet.getClass().getSimpleName());
        });
    }

    @Override
    public void tick() {
        //Notification.create((long) (Math.random() * 10000), "amogus", "sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus sus");
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public String getContext() {
        return "amog stuff";
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
    }

    @Override
    public void onHudRender() {
    }

}
