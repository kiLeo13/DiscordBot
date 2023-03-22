package bot.util;

import java.util.ArrayList;
import java.util.List;

public class Roles {
    private Roles() {}

    public static final long ROLE_STAFF = 691178135596695593L;
    public static final List<Long> ROLES_EVENTOS = List.of(648408514242543617L, 648408509985325082L, 691167800605016095L, 740360653075906610L, 648444769026048010L);
    public static final List<Long> ROLES_RADIO = List.of(648408513319534632L, 640215067786346527L, 691167798981820447L, 740360647845478447L, 648444765079207944L);
    public static final List<Long> ROLE_RADIO_AND_EVENTOS = bothEventosRadio();

    private static List<Long> bothEventosRadio() {
        List<Long> finalList = new ArrayList<>();
        finalList.addAll(ROLES_EVENTOS);
        finalList.addAll(ROLES_RADIO);
        return finalList;
    }
}