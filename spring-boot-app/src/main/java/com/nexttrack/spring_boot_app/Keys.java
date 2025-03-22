package com.nexttrack.spring_boot_app;
import io.github.cdimascio.dotenv.Dotenv;

public class Keys {
    private static final Dotenv dotenv = Dotenv.load(); // loads .env file

    public static String getKey(String key) {
        return dotenv.get(key);
    }
}
