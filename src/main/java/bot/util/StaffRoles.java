package bot.util;

import java.util.List;

public enum StaffRoles {
    GENERAL_AJUDANTES(List.of(648444762852163588L, 740360642032173156L, 691167798474440775L, 592427681727905792L, 648408508219260928L)),

    ROLE_STAFF(691178135596695593L),
    ROLES_EVENTOS(List.of(648408514242543617L, 648408509985325082L, 691167800605016095L, 740360653075906610L, 648444769026048010L));

    List<Long> ids;
    long id;

    StaffRoles(List<Long> ids) {
        this.ids = ids;
    }

    StaffRoles(long id) {
        this.id = id;
    }

    public List<Long> toIds() {
        return this.ids;
    }

    public long toId() {
        return this.id;
    }
}