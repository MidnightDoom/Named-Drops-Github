package io.github.midnightdoom.named_drops;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class ModConfig {
    /*
    private static final Gson GSON = new Gson();
    private static final File CONFIG_FILE = new File("config/named_drops_config.json");

    private static Set<Identifier> includedItemIds = new HashSet<>();
    private static RenameRule renameRule = RenameRule.UNSTACKABLES_PLUS_LIST;
    private static RenameRule modRenameRule = RenameRule.UNSTACKABLES_PLUS_LIST;

    public static void load() {
        try {
            if (!CONFIG_FILE.exists()) {
                try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                    writer.write("{\n\"item_list\":[], \n \"rename_rule\": \"UNSTACKABLE_PLUS_LIST\", \n \"mob_rename_rule\": \"ALL\"\n}");
                }
            }

            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<ConfigData>() {}.getType();
                ConfigData config = GSON.fromJson(reader, type);

                includedItemIds.clear();
                for (String id : config.item_list) {
                    includedItemIds.add(new Identifier(id));
                }
                renameRule = RenameRule.fromString(config.rename_rule);
                modRenameRule = RenameRule.fromString(config.mod_rename_rule);
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    public static boolean isIncluded(Item item) {
        return includedItemIds.contains(Registries.ITEM.getId(item));
    }

    public static RenameRule rule() {
        return renameRule;
    }

    public static RenameRule mobRenameRule() {
        return modRenameRule;
    }

    private static class ConfigData {
        public Set<String> item_list = new HashSet<>();
        public String rename_rule = "UNSTACKABLE_PLUS_LIST";
        public String mod_rename_rule = "ALL";
    }

     */
    private static final File CONFIG_FILE = new File("config/named_drops_config.json");
    private static final Gson GSON = new Gson();

    private static final Map<UUID, PlayerRenameConfig> perPlayerConfigs = new HashMap<>();
    private static PlayerRenameConfig defaultConfig = new PlayerRenameConfig();
    private static Set<Identifier> includedItemIds = new HashSet<>();

    public static void load() {
        try {
            if (!CONFIG_FILE.exists()) {
                try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                    writer.write("{\n" +
                            "  \"default\": {\n" +
                            "    \"mob_rename_rule\": \"ALL\",\n" +
                            "    \"rename_rule\": \"UNSTACKABLES_PLUS_LIST\",\n" +
                            "    \"item_list\": []\n" +
                            "  }\n" +
                            "}");
                }
            }

            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<String, PlayerRenameConfig>>() {}.getType();
                Map<String, PlayerRenameConfig> raw = GSON.fromJson(reader, type);

                perPlayerConfigs.clear();
                for (Map.Entry<String, PlayerRenameConfig> entry : raw.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("default")) {
                        defaultConfig = entry.getValue();
                    } else {
                        try {
                            UUID uuid = UUID.fromString(entry.getKey());
                            perPlayerConfigs.put(uuid, entry.getValue());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid UUID in config: " + entry.getKey());
                        }
                    }
                }

                System.out.println(defaultConfig.item_list);
                System.out.println(defaultConfig.mob_rename_rule);
                System.out.println(defaultConfig.rename_rule);
            }

        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    public static PlayerRenameConfig getConfigFor(UUID uuid) {
        return perPlayerConfigs.getOrDefault(uuid, defaultConfig);
    }

    public static PlayerRenameConfig getDefaultConfig() {
        return defaultConfig;
    }

    public static boolean isIncluded(Item item, PlayerRenameConfig config) {
        return config.item_list.contains(Registries.ITEM.getId(item).toString());
    }

    public static RenameRule rule(PlayerRenameConfig config) {
        return RenameRule.fromString(config.rename_rule);
    }

    public static RenameRule mobRenameRule() {
        return RenameRule.fromString(defaultConfig.mob_rename_rule);
    }

    public static class PlayerRenameConfig {
        public String rename_rule = "UNSTACKABLE_PLUS_LIST";
        public String mob_rename_rule = "ALL";
        public Set<String> item_list = new HashSet<>();
    }
}



