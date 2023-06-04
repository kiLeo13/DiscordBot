package bot.util.content;

public enum Categories {
    SUPPORT("691788421697503322");

    final String id;

    Categories(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}