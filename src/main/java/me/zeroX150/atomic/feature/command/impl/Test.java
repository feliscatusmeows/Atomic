/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.screen.MessageScreen;
import me.zeroX150.atomic.helper.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "amogus sus", "among", "sus", "test");
    }

    @Override
    public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(10, () -> Atomic.client.setScreen(new MessageScreen(null, "cum", "do you ever just\nfard", System.out::println, MessageScreen.ScreenType.OK)));
    }
}
