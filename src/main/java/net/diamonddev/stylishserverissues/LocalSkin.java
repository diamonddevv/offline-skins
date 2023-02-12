package net.diamonddev.stylishserverissues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.DefaultSkinHelper;

@Environment(EnvType.CLIENT)
public record LocalSkin(String localPath, DefaultSkinHelper.Model model) {
}
