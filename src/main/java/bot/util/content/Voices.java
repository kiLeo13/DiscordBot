package bot.util.content;

import java.util.List;

public enum Voices {
    SALADA("693627612454453250");

    final String[] ids;

    Voices(String... ids) {
        this.ids = ids;
    }

    public String id() {
        return this.ids.length == 0
                ? ""
                : this.ids[0];
    }

    public List<String> ids() {
        return List.of(this.ids);
    }
}