/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.screen.ItemsScreen;
import me.zeroX150.atomic.helper.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "amogus sus", "among", "sus", "test");
    }

    public static void real() {
        /*WindowScreen ws = new WindowScreen("REAL");
        for (int i = 0; i < 20; i++) {
            Window t = new Window("THE " + i, true, i, 5, 100, 100, i % 2 == 0);
            for(int ii=0;ii<i;ii++) {
                ButtonWidget bw = new ButtonWidget(0, 20*ii, 100, 20, Text.of("THE SEX!!!"), button -> {

                });
                t.addChild(bw);
            }
            ws.addWindow(t);
        }
        Atomic.client.setScreen(ws);*/
        Atomic.client.setScreen(new ItemsScreen());
    }

    @Override
    public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(10, Test::real);
    }
}
