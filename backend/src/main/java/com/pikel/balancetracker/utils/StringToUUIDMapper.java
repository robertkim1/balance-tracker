package com.pikel.balancetracker.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StringToUUIDMapper {
    private static final UUID NAMESPACE =
            UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    public static UUID fromBetterAuthId(String betterAuthId) {
        return UUID.nameUUIDFromBytes(
                (NAMESPACE + betterAuthId).getBytes(StandardCharsets.UTF_8)
        );
    }
}
