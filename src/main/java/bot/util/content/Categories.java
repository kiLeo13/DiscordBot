package bot.util.content;

public enum Categories {
    SUPPORT("1114709273495212152");

    final String id;

    Categories(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}