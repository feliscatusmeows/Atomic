package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.Utils;

import java.util.Objects;

public class Say extends Command {
    public Say() {
        super("Say", "Says something", "say", "tell");
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("not sure if i can say nothing");
            return;
        }
        Objects.requireNonNull(Atomic.client.player).sendChatMessage(String.join(" ", args));
    }
}
