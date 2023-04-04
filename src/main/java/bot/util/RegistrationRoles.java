package bot.util;

public enum RegistrationRoles {
    ROLE_REQUIRED(740360659363168287L, "⚙"),

    ROLE_REGISTERED(664923267601006622L, "✅"),
    ROLE_NOT_REGISTERED(664921745777623088L, "❌"),
    ROLE_VERIFIED(758095502599520328L, "❌"),

    ROLE_FEMALE(664916190082236427L, "✅"),
    ROLE_MALE(664916190904320000L, "✅"),
    ROLE_NON_BINARY(664916189029466122L, "✅"),

    ROLE_ADULT(664918505963126814L, "✅"),
    ROLE_UNDERAGE(664918505400958986L, "✅"),
    ROLE_UNDER13(758095500884049960L, "✅"),

    ROLE_COMPUTER(664917764229824512L, "✅"),
    ROLE_MOBILE(664917765395578892L, "✅");

    final long roleId;
    final String emoji;

    RegistrationRoles(long roleId, String emoji) {
        this.roleId = roleId;
        this.emoji = emoji;
    }

    public long toId() { return this.roleId; }

    public String emoji() { return this.emoji; }
}