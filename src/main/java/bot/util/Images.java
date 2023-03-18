package bot.util;

import bot.Main;

import java.io.File;
import java.net.URL;

public class Images {
    private Images() {}

    public static final File LAPADA = getImage("lapada.png");
    public static final File TAPA = getImage("tapa.png");

    private static File getImage(String fileName) {
        URL file = Main.class.getResource("/content/images/" + fileName);

        if (file == null) return null;

        return new File(file.getFile());
    }
}