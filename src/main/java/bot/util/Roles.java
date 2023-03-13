package bot.util;

public enum Roles {
    ROLE_REQUIRED("1009140499325648991"),

    ROLE_FEMALE("664916190082236427"),
    ROLE_MALE("664916190904320000"),
    ROLE_NON_BINARY("664916189029466122"),

    ROLE_ADULT("664918505963126814"),
    ROLE_UNDERAGE("664918505400958986"),
    ROLE_UNDER13("758095500884049960"),

    ROLE_COMPUTER(""),
    ROLE_MOBILE("");

    final String role;

    Roles(String role) { this.role = role; }

    public String get() { return this.role; }
}