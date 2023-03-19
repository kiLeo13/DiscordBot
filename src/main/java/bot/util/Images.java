package bot.util;

import bot.Main;

import java.net.URL;

public class Images {
    private Images() {}

    public static final URL LAPADA = file("lapada.png");
    public static final URL TAPA = file("tapa.png");

    private static URL file(String fileName) {
        return Main.class.getResource("/content/images/" + fileName);
    }
}