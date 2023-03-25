package bot.util;

import java.util.List;

public enum StaffField {
    GENERAL_AJUDANTES(List.of(648444762852163588L, 740360642032173156L, 691167798474440775L, 592427681727905792L, 648408508219260928L));

    final List<Long> ids;

    StaffField(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> toIds() {
        return this.ids;
    }
}