package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.ImGuiManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


/**
 * IT IS VERY IMPORTANT TO INSTANCE THIS SCREEN ONLY ONCE AND NEVER AGAIN BECAUSE IMGUI WILL SHIT PANT
 */
public abstract class ImGuiProxyScreen extends Screen {
    //    protected final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    //    protected final ImGuiImplGl3  implGl3  = new ImGuiImplGl3();
    public ImGuiProxyScreen() {
        super(Text.of(""));
        ImGuiManager.init();
        //        long win = Atomic.client.getWindow().getHandle();
        //        ImGui.createContext();
        //        ImGui.getIO().getFonts().addFontFromMemoryTTF(getMainFont(),18);
        //        implGlfw.init(win,true);
        //        implGl3.init();
        //        ImGui.getIO().setConfigWindowsMoveFromTitleBarOnly(true);
        //        ImGui.getStyle().setWindowMenuButtonPosition(-1);
    }

    //    private static byte[] getMainFont() {
    //        try {
    //            return Files.readAllBytes(Paths.get(ImGuiProxyScreen.class.getClassLoader().getResource("Font.ttf").toURI()));
    //        } catch (IOException | URISyntaxException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }
    protected abstract void renderInternal();

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // sets the size of the window in case it got resized
        ImGui.getIO().setDisplaySize(Atomic.client.getWindow().getWidth(), Atomic.client.getWindow().getHeight());
        // new frame
        ImGuiManager.getImplGlfw().newFrame();
        ImGui.newFrame();

        renderInternal(); // pass it to homeboy

        // end the frame
        ImGui.endFrame();
        // draw
        ImGui.render();
        ImGuiManager.getImplGl3().renderDrawData(ImGui.getDrawData());
    }
}
