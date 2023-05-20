package bot.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YamlUtil {
    private static final Yaml yaml = new Yaml();

    public static List<String> readAllowedLinks() {
        final File file = new File("resources", "allowedlinks.yml");

        try (InputStream inputStream = new FileInputStream(file)) {
            HashMap<String, List<String>> data = yaml.load(inputStream);

            return data == null || data.get("allowed") == null
                    ? new ArrayList<>()
                    : data.get("allowed");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<String> readBlockedLinks() {
        final File file = new File("resources", "allowedlinks.yml");

        try (InputStream inputStream = new FileInputStream(file)) {
            HashMap<String, List<String>> data = yaml.load(inputStream);

            return data == null || data.get("blocked") == null
                    ? new ArrayList<>()
                    : data.get("blocked");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}