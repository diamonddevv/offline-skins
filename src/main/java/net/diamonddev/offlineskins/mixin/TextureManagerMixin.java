package net.diamonddev.offlineskins.mixin;

import net.diamonddev.offlineskins.OfflineSkins;
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
    private void offlineskins$cacheSkin(ResourceManager resourceManager, CallbackInfo ci) {
        if (OfflineSkins.USES_METADATA_SKIN) {
            try {
                OfflineSkins.copySkinFromMemoryToCache((TextureManager) (Object) this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
