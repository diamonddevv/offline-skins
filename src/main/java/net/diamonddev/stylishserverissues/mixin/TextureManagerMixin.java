package net.diamonddev.stylishserverissues.mixin;

import net.diamonddev.stylishserverissues.StylishServerIssues;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void stylishserverissues$cacheSkin(ResourceManager resourceManager, CallbackInfo ci) {
        if (StylishServerIssues.USES_METADATA_SKIN) {
            try {
                StylishServerIssues.copySkinFromMemoryToCache((TextureManager) (Object) this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
