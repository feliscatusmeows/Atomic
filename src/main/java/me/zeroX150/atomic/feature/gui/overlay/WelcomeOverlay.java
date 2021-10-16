/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.overlay;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.HomeScreen;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class WelcomeOverlay extends Overlay {
    final String[] texts = new String[]{
            "Loading...",
            "Welcome, " + Atomic.client.getSession().getUsername() + ", to Atomic."
    };
    final List<LogEntry> logs = new ArrayList<>();
    double d = 0;
    boolean done = false;
    boolean decl = false;
    float prevVal = 0;
    boolean finishedLoading = false;
    boolean isLoading = false;
    Thread loader;

    void log(String v) {
        logs.add(new LogEntry(v));
    }

    void downloadFile(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    void load() {
        if (isLoading) return;
        isLoading = true;
        loader = new Thread(() -> {
            try {
                for (Module module : ModuleRegistry.getModules().stream().sorted(Comparator.comparingDouble(value -> -Atomic.monoFontRenderer.getStringWidth(value.getName()))).collect(Collectors.toList())) {
                    log("Loaded module " + module.getName());
                    Thread.sleep(20);
                }
                log("Downloading capes...");
                File capes = new File(Atomic.client.runDirectory.getAbsolutePath() + "/aCapes");
                if (!capes.isDirectory()) capes.delete();
                if (!capes.exists()) capes.mkdir();
                Map<String, String> alreadyDownloadedWithFN = new HashMap<>();
                //List<String> alreadyDownloaded = new ArrayList<>();
                HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://raw.githubusercontent.com/cornos/atomicFiles/master/capes.txt"))
                        .timeout(Duration.ofSeconds(10))
                        .GET().build();
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(s -> {
                    for (String s1 : s.split("\n")) { // all capes, dumped into here
                        if (s1.startsWith("#")) continue; // ignore comments
                        Atomic.log(Level.INFO, s1);
                        String[] split = s1.split(" +"); // split everything at a space
                        if (split.length != 2) continue; // we only want "uuid capeUrl" format
                        String uuid = split[0];
                        String capeUrl = split[1];
                        try {
                            UUID u = UUID.fromString(uuid);
                            if (alreadyDownloadedWithFN.containsKey(capeUrl)) {
                                log("Skipping " + uuid + " because already downloaded");
                                Atomic.capes.put(u, alreadyDownloadedWithFN.get(capeUrl));
                                continue;
                            }
                            log("Downloading for " + uuid);
                            downloadFile(capeUrl, "aCapes/" + u + ".png");
                            alreadyDownloadedWithFN.put(capeUrl, u + ".png");
                            Atomic.capes.put(u, u + ".png");
                        } catch (Exception ignored) {
                            log("Invalid UUID entry \"" + uuid + "\"");
                        }
                    }
                    Atomic.log(Level.INFO, "-- Cape mappings --");
                    Atomic.capes.forEach((uuid, s1) -> Atomic.log(Level.INFO, "  " + uuid + " has cape at " + s1));
                    finishedLoading = true;
                }).exceptionally(throwable -> {
                    log("Failed to download capes!");
                    finishedLoading = true;
                    return null;
                });
            } catch (Exception ignored) {

            }
        });
        loader.start();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = Atomic.client.getWindow().getScaledWidth();
        int h = Atomic.client.getWindow().getScaledHeight();
        if (done) {
            d = 0;
            done = false;
            decl = false;
            Atomic.client.setOverlay(null);
            return;
        }

        double vd = 2.35 * delta;
        if (d > 90) {
            if (finishedLoading) d += vd;
            else {
                load();
            }
        } else d += vd;
        float c = (float) Math.abs(Math.sin(Math.toRadians(d)));
        int index = (int) Math.floor(d / 180);
        int c1 = Color.BLACK.getRGB();
        int a = 255;
        if (index >= texts.length - 1) {
            if (prevVal > c) decl = true;
            if (decl) {
                if (Atomic.client.currentScreen == null) Atomic.client.setScreen(new HomeScreen());
                Atomic.client.currentScreen.render(matrices, mouseX, mouseY, delta);
                c1 = BackgroundHelper.ColorMixer.getArgb((int) (c * 255), 0, 0, 0);
                a = (int) (c * 255);
            }
            prevVal = c;
        }
        DrawableHelper.fill(matrices, 0, 0, w, h, c1);
        if (index >= texts.length) {
            done = true;
            return;
        }
        if (c > 0.07) {
            if (index == 1) {
                if (logs.size() > 0) {
                    for (LogEntry log : logs) {
                        if (!log.removed) {
                            log.removed = true;
                            break;
                        }
                    }
                }
            }
            Atomic.fontRenderer.drawCenteredString(matrices, texts[index], w / 2f, h / 2f, BackgroundHelper.ColorMixer.getArgb((int) (c * 255), 255, 255, 255));
            //DrawableHelper.drawCenteredText(matrices, Atomic.client.textRenderer, texts[index], (int) ((w / 2) / m), (int) ((h / 2 - (9 / 2)) / m), BackgroundHelper.ColorMixer.getArgb((int) (c * 255), 255, 255, 255));
        }
        float scale = 1f;
        while (logs.size() > (Atomic.client.getWindow().getScaledHeight() - 1) / (10 * scale)) {
            logs.remove(0);
        }
        int yOffset = 1;
        for (LogEntry log : logs.toArray(new LogEntry[0])) {
            log.aProg = Transitions.transition(log.aProg, log.removed ? 0 : 1, 7, 0);
            matrices.push();
            matrices.scale(scale, scale, 1);
            double wid = Atomic.monoFontRenderer.getStringWidth(log.a) + 2;
            float sub = (float) ((1 - log.aProg) * wid);
            Atomic.monoFontRenderer.drawString(matrices, log.a, 1 - sub, yOffset, BackgroundHelper.ColorMixer.getArgb(MathHelper.clamp(a, 1, 255), 255, 255, 255));
            yOffset += 10;
            matrices.pop();
        }
    }

    static class LogEntry {
        final String a;
        boolean removed = false;
        double aProg = 0;

        public LogEntry(String v) {
            a = v;
        }
    }
}
