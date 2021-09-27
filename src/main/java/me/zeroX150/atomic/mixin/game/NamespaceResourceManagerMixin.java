package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.Atomic;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;

@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
    @Inject(method = "getResource", cancellable = true, at = @At("HEAD"))
    public void gr(Identifier id, CallbackInfoReturnable<Resource> cir) {
        if (id.getNamespace().equalsIgnoreCase("atomic") && id.getPath().startsWith("capes/")) {
            String sanitizedPath = id.getPath().replace("capes/", "");
            File f = new File(Atomic.client.runDirectory.getAbsolutePath() + "/aCapes/" + sanitizedPath);
            if (!f.exists() || !f.isFile()) cir.setReturnValue(null);
            else {
                try {
                    cir.setReturnValue(new ResourceImpl("atomicCapes", id, f.toURI().toURL().openStream(), null));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
