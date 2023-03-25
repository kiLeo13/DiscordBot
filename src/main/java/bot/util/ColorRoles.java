package bot.util;

public enum ColorRoles {
    FIRE_ELEMENT(1086379389362638979L),
    EARTH_ELEMENT(1086379420681506968L),
    WATER_ELEMENT(1086379439954333746L),
    LIGHT_ELEMENT(1086379457893384272L),
    AIR_ELEMENT(1086379490764148756L);

    final long id;

    ColorRoles(long id) {
        this.id = id;
    }

    public long toLong() {
        return this.id;
    }
}