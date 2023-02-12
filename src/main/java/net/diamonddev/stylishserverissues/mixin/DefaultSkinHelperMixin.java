package net.diamonddev.stylishserverissues.mixin;

import net.diamonddev.stylishserverissues.StylishServerIssues;
import net.minecraft.client.util.DefaultSkinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
public class DefaultSkinHelperMixin {

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private static void stylishserverissues$loadSkin(UUID uuid, CallbackInfoReturnable<DefaultSkinHelper.Skin> cir) {
        if (StylishServerIssues.USES_METADATA_SKIN) {
            cir.setReturnValue(StylishServerIssues.getNonLocalSkin());
        }
    }
}
