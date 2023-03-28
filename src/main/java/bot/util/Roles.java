package bot.util;

import java.util.List;

public enum Roles {
    ROLES_RADIO(List.of(648408513319534632L, 640215067786346527L, 691167798981820447L, 740360647845478447L, 648444765079207944L)),

    ROLE_AMONG_US(596784802150088704L);

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