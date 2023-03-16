package bot.util;

public enum Roles {
    ROLE_REQUIRED("1085378510534746202"),

    ROLE_REGISTERED("1085378345287569418"),
    ROLE_NOT_REGISTERED("1085378399175970938"),
    ROLE_VERIFIED("1085378446047313980"),

    ROLE_FEMALE("1085378073991598141"),
    ROLE_MALE("1085378055213695077"),
    ROLE_NON_BINARY("1085378010561126481"),

    ROLE_ADULT("1085378246855626822"),
    ROLE_UNDERAGE("1085378160289382430"),
    ROLE_UNDER13("1085378212999221318"),

    ROLE_COMPUTER("1085378125350834177"),
    ROLE_MOBILE("1085378097928478720");

    final String roleId;

    Roles(String roleId) { this.roleId = roleId; }

    public String get() { return this.roleId; }
}