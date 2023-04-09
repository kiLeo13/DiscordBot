package bot.util;

import java.util.List;

public enum Roles {
    ROLE_AMONG_US(596784802150088704L),
    ROLE_SALADA(693973471989858714L),
    ROLE_ALFEA(777327585293631499L);

    long id;
    List<Long> ids;

    Roles(long id) {
        this.id = id;
    }

    Roles(List<Long> ids) {
        this.ids = ids;
    }

    public long toId() {
        return this.id;
    }

    public List<Long> toIds() {
        return this.ids;
    }
}