package io.github.midnightdoom.named_drops;

import java.util.Locale;

public enum RenameRule {
    ALL,
    UNSTACKABLES,
    UNSTACKABLES_PLUS_LIST,
    LIST_ONLY;

    public static RenameRule fromString(String str) {
        try {
            return RenameRule.valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNSTACKABLES_PLUS_LIST; // default
        }
    }
}
