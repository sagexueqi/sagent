package ai.sagesource.base.utils;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * DotEnv Utils
 *
 * @author: sage.xue
 * @time: 2026/2/4
 */
public class DotEnvUtils {

    public static Dotenv loadEnv() {
        return Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();
    }
}
