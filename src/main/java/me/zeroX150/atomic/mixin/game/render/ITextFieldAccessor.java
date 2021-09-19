package me.zeroX150.atomic.mixin.game.render;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextFieldWidget.class)
public interface ITextFieldAccessor {
    @Accessor("maxLength")
    int getMaxLength();

    @Invoker("onFocusedChanged")
    void onFocusChanged(boolean newFocused);
}
