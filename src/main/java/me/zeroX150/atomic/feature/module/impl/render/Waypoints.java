package me.zeroX150.atomic.feature.module.impl.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.render.Color;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Waypoints extends Module {
    public static final File WAYPOINTS_FILE;
    static final String TOP_NOTE = """
            // ! WARNING
            // ! THIS IS SENSITIVE INFORMATION
            // ! DO NOT GIVE SOMEONE THIS FILE, IF THEY ASK FOR IT FOR NO REASON
            // ! DO ALSO NOT TAMPER WITH THIS FILE, IT COULD BREAK THE CONFIG
            """;
    static final List<Waypoint> waypoints = new ArrayList<>();

    static {
        WAYPOINTS_FILE = new File(Atomic.client.runDirectory.getAbsolutePath() + "/waypoints.atomic");
    }

    final BooleanValue tracers = (BooleanValue) this.config.create("Tracers", true).description("Show tracers to the waypoints");

    public Waypoints() {
        super("Waypoints", "Saves positions", ModuleType.MISC);
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> { // gets called when we save config files
            Atomic.log(Level.INFO, "Saving " + waypoints.size() + " waypoints...");
            JsonObject base = new JsonObject();
            JsonArray wayp = new JsonArray();
            for (Waypoint waypoint : waypoints) {
                JsonObject current = new JsonObject();
                current.addProperty("posX", waypoint.posX);
                current.addProperty("posZ", waypoint.posZ);
                current.addProperty("color", waypoint.color);
                current.addProperty("name", waypoint.name);
                wayp.add(current);
            }
            base.add("waypoints", wayp);
            try {
                FileUtils.writeStringToFile(WAYPOINTS_FILE, TOP_NOTE + base, StandardCharsets.UTF_8);
            } catch (IOException e) {
                Atomic.log(Level.ERROR, "Failed to save waypoints!");
            }
        });
        Atomic.log(Level.INFO, "Loading waypoints..."); // gets called when we init the modules, before we load the config
        if (!WAYPOINTS_FILE.exists()) {
            Atomic.log(Level.WARN, "Waypoints file not found, first run or reset?");
            return;
        }
        if (!WAYPOINTS_FILE.isFile()) {
            Atomic.log(Level.WARN, "Waypoints \"file\" is not actually a file, resetting..");
            boolean deleted = WAYPOINTS_FILE.delete();
            if (!deleted) Atomic.log(Level.ERROR, "Failed to delete waypoints file, what the fuck is going on?");
        }

        try {
            String data = FileUtils.readFileToString(WAYPOINTS_FILE, StandardCharsets.UTF_8);
            JsonObject jo = new JsonParser().parse(data).getAsJsonObject();
            JsonArray ja = jo.getAsJsonArray("waypoints");
            for (JsonElement jsonElement : ja) {
                JsonObject current = (JsonObject) jsonElement;
                Waypoint w = new Waypoint(current.get("posX").getAsDouble(),
                        current.get("posZ").getAsDouble(),
                        current.get("color").getAsInt(),
                        current.get("name").getAsString());
                waypoints.add(w);
            }
        } catch (IOException e) {
            Atomic.log(Level.ERROR, "Failed to read waypoints file!");
        }
    }

    public static List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public static void addWaypoint(Waypoint point) {
        waypoints.add(point);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        // 6 hours.
        // im gonna kms
        Vec3d ppos = Objects.requireNonNull(Atomic.client.player).getPos();
        for (Waypoint waypoint : new ArrayList<>(getWaypoints())) {
            Camera c = Atomic.client.gameRenderer.getCamera();
            Vec3d v = new Vec3d(waypoint.posX, 0, waypoint.posZ);
            int r = BackgroundHelper.ColorMixer.getRed(waypoint.color);
            int g = BackgroundHelper.ColorMixer.getGreen(waypoint.color);
            int b = BackgroundHelper.ColorMixer.getBlue(waypoint.color);
            Vec3d vv = new Vec3d(waypoint.posX + .5, c.getPos().y, waypoint.posZ + .5);
            if (tracers.getValue()) Renderer.line(vv, Renderer.getCrosshairVector(), new Color(r, g, b), matrices);
            double distance = vv.distanceTo(ppos);
            int a = 255;
            float scale = 3f;
            scale /= 50f;
            scale *= 0.55f;
            if (distance < 10) {
                a = (int) ((distance / 10) * 255);
            } else scale *= distance / 10d;
            Renderer.renderFilled(v, new Vec3d(1, 255, 1), new Color(r, g, b, a), matrices);
            Vec3d textPos = new Vec3d(waypoint.posX + .5, c.getPos().y, waypoint.posZ + .5);
            matrices.push();
            matrices.translate(textPos.x - c.getPos().x, textPos.y - c.getPos().y, textPos.z - c.getPos().z);
            matrices.translate(0, scale * 6, 0);
            matrices.scale(-scale, -scale, scale);
            Quaternion qu = Atomic.client.getEntityRenderDispatcher().getRotation();
            qu = new Quaternion(-qu.getX(), -qu.getY(), qu.getZ(), qu.getW());
            matrices.multiply(qu);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            RenderSystem.disableDepthTest();
            float yf = Atomic.client.textRenderer.getWidth(waypoint.name);
            Renderer.fill(matrices, Themes.Theme.ATOMIC.getPalette().inactive(), -yf / 2f - 2, -2, yf / 2f + 1, 9);
            RenderSystem.polygonOffset(1, -15000000);
            RenderSystem.enablePolygonOffset();
            Atomic.client.textRenderer.draw(matrices, waypoint.name, -yf / 2f, 0, 0xFFFFFF);
            RenderSystem.polygonOffset(1, 15000000);
            RenderSystem.disablePolygonOffset();
            RenderSystem.enableDepthTest();
            immediate.draw();
            matrices.pop();
        }
    }

    @Override
    public void onHudRender() {

    }

    public static record Waypoint(double posX, double posZ, int color, String name) {
    }
}

