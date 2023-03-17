package bot.util;

import bot.Main;

import java.io.File;
import java.net.URL;

public enum Images {
    LAPADA(getImage("lapada.png")),
    TAPA(getImage("tapa.png"));

    private final File file;
    Images(File file) { this.file = file; }

    public File get() { return file; }

    private static File getImage(String fileName) {
        URL file = Main.class.getResource("/content/images/" + fileName);

        if (file == null) return null;

        return new File(file.getFile());
    }
}