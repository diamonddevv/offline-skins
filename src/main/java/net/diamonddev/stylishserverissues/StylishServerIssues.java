package net.diamonddev.stylishserverissues;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.Scanner;

public class StylishServerIssues implements ClientModInitializer {

    public static final String modid = "stylishserverissues";
    public static final Logger LOGGER = LoggerFactory.getLogger("Stylish Server Issues");

    public static final Gson gson = new Gson();


    public static SkinMetadata METADATA;
    public static boolean USES_METADATA_SKIN = false;
    public static final Identifier CACHED_SKIN_ID = new Identifier(modid, "cached_skin");

    @Override
    public void onInitializeClient() {
        long start = System.currentTimeMillis();
        //

        try {

            load();

            if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment()) {
                LOGGER.info("## Cached Skin Metadata ##");
                LOGGER.info("## Slim Arms?: " + METADATA.slim_model);
                LOGGER.info("## Filename: " + METADATA.filename);
                LOGGER.info("##########################");
            }

        } catch (IOException e) {
            USES_METADATA_SKIN = false;
            LOGGER.error("Could not load skin with metadata. Using default system..");
        }

        //
        long initTime = System.currentTimeMillis() - start;
        LOGGER.info("Mod " + modid + " initialized in " + initTime + " millisecond(s)!");
    }

    public static void load() throws IOException {
        File file = getLocalFolder();
        boolean madeDDVDir = file.mkdir();
        if (madeDDVDir) LOGGER.info(".diamonddev directory was not found, and has been created.");

        file = new File(file + "\\stylish-server-issues");
        boolean madeDir = file.mkdir();
        if (madeDir) LOGGER.info("stylish-server-issues directory was not found, and has been created.");

        File metadataFile = new File(file + "\\skin_metadata.json");
        if (metadataFile.createNewFile()) LOGGER.info("skin_metadata.json was not found, and has been created.");

        LOGGER.info("Loading skin metadata..");
        JsonReader fileReader = new JsonReader(new FileReader(metadataFile));
        Scanner fileScan = new Scanner(metadataFile);
        if (!fileScan.hasNext()) {
            LOGGER.info("Populating metadata..");
            FileWriter write = new FileWriter(metadataFile);

            write.write("""
                    {
                        "slim_model": false,
                        "filename": "skin.png"
                    }
                    """);

            write.close();
        }
        fileScan.close();

        METADATA = decode(fileReader);

        LOGGER.info("Checking if '{}' exists..", METADATA.filename);
        if (!new File(file + "\\" + METADATA.filename).exists()) {
            LOGGER.error("{} did not exist!", METADATA.filename);
            throw new IOException();
        }

        USES_METADATA_SKIN = true;
        LOGGER.info("Loaded metadata!");
        fileReader.close();
    }
    public static File getLocalFolder() {
        return new File(FabricLoader.getInstance().getConfigDir() + "\\.diamonddev");
    }

    public static LocalSkin constructSkinData(SkinMetadata metadata) {
        return new LocalSkin(getLocalFolder() + "\\stylish-server-issues\\" + metadata.filename, metadata.slim_model ? DefaultSkinHelper.Model.SLIM : DefaultSkinHelper.Model.WIDE);
    }
    public static DefaultSkinHelper.Model getModel() {
        return METADATA.slim_model ? DefaultSkinHelper.Model.SLIM : DefaultSkinHelper.Model.WIDE;
    }

    public static DefaultSkinHelper.Skin getNonLocalSkin() {
        return new DefaultSkinHelper.Skin(CACHED_SKIN_ID.toString(), getModel());
    }

    public static void copySkinFromMemoryToCache(TextureManager manager) throws IOException {
        LocalSkin localskin = constructSkinData(METADATA);
        LOGGER.info("Copying {} to texture cache..", localskin.localPath());
        manager.registerTexture(CACHED_SKIN_ID, new NativeImageBackedTexture(NativeImage.read(new FileInputStream(localskin.localPath()))));
        LOGGER.info("Copied {} to texture cache successfully! (Located at {})", localskin.localPath(), CACHED_SKIN_ID);
    }

    private static SkinMetadata decode(JsonReader metadataFile) {
        return gson.fromJson(metadataFile, SkinMetadata.class);
    }

    public static class SkinMetadata {
        boolean slim_model;
        String filename;
    }
}
