package net.diamonddev.offlineskins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.ClientModInitializer;
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

public class OfflineSkins implements ClientModInitializer {

    public static final String modid = "offlineskins";
    public static final Logger LOGGER = LoggerFactory.getLogger("Offline Skins");

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
                LOGGER.info("## OfflineSkin Metadata ##");
                LOGGER.info("## Slim Arms?: " + METADATA.slimarms);
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

        file = new File(file + "\\offline-skins");
        boolean madeDir = file.mkdir();
        if (madeDir) LOGGER.info("offline-skins directory was not found, and has been created.");

        File metadataFile = new File(file + "\\skin_metadata.json");
        if (metadataFile.createNewFile()) LOGGER.info("skin_metadata.json was not found, and has been created.");

        LOGGER.info("Loading skin metadata..");
        metadataFile = new File(file + "\\skin_metadata.json");
        JsonReader fileReader = new JsonReader(new FileReader(metadataFile));
        if (!fileReader.hasNext()) {
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

        METADATA = decode(fileReader);
        USES_METADATA_SKIN = true;
        LOGGER.info("Loaded metadata!");
    }
    public static File getLocalFolder() {
        return new File(System.getenv("APPDATA") + "\\.diamonddev");
    }

    public static LocalSkin constructSkinData(SkinMetadata metadata) {
        return new LocalSkin(getLocalFolder() + "\\offline-skins\\" + metadata.filename, metadata.slimarms ? DefaultSkinHelper.Model.SLIM : DefaultSkinHelper.Model.WIDE);
    }
    public static DefaultSkinHelper.Model getModel() {
        return METADATA.slimarms ? DefaultSkinHelper.Model.SLIM : DefaultSkinHelper.Model.WIDE;
    }

    public static DefaultSkinHelper.Skin getNonLocalSkin() {
        return new DefaultSkinHelper.Skin(CACHED_SKIN_ID.toString(), getModel());
    }

    public static void copySkinFromMemoryToCache(TextureManager manager) throws IOException {
        LocalSkin localskin = constructSkinData(METADATA);

        manager.registerTexture(CACHED_SKIN_ID, new NativeImageBackedTexture(NativeImage.read(new FileInputStream(localskin.localPath()))));
    }

    private static final String JSON_KEY_SLIM_ARMS = "slim_model";
    private static final String JSON_KEY_FILE = "filename";

    private static SkinMetadata decode(JsonReader metadataFile) {
        JsonObject json = gson.fromJson(metadataFile, JsonObject.class);
        return new SkinMetadata(json.get(JSON_KEY_SLIM_ARMS).getAsBoolean(), json.get(JSON_KEY_FILE).getAsString());
    }

    public record SkinMetadata(boolean slimarms, String filename) {
    }
}
