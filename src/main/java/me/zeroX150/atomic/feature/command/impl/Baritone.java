/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import baritone.api.BaritoneAPI;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.Utils;

import java.util.Objects;

public class Baritone extends Command {
    public Baritone() {
        super("Baritone", "Runs baritone commands", "baritone", "b");
    }

    @Override
    public void onExecute(String[] args) {
        String v = Utils.getValueFromBaritoneSetting(BaritoneAPI.getSettings().prefix); // what the fuck is the issue?
        Objects.requireNonNull(Atomic.client.player).sendChatMessage(v + String.join(" ", args));
    }
}
