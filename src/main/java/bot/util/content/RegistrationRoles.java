package bot.util.content;

public enum RegistrationRoles {
    ROLE_REQUIRED(740360659363168287L, "Registrador", "⚙"),

    ROLE_REGISTERED(664923267601006622L, "Registrado", "✅"),
    ROLE_NOT_REGISTERED(664921745777623088L, "Não registrado", "❌"),
    ROLE_VERIFIED(758095502599520328L, "Verificado", "❌"),

    ROLE_FEMALE(664916190082236427L, "Feminino", "✅"),
    ROLE_MALE(664916190904320000L, "Masculino", "✅"),
    ROLE_NON_BINARY(664916189029466122L, "Não binário", "✅"),

    ROLE_ADULT(664918505963126814L, "Maior de idade", "✅"),
    ROLE_UNDERAGE(664918505400958986L, "Menor de idade", "✅"),
    ROLE_UNDER13(758095500884049960L, "😻", "✅"),

    ROLE_COMPUTER(664917764229824512L, "Computador", "✅"),
    ROLE_MOBILE(664917765395578892L, "Mobile", "✅");

    final long roleId;
    final String identifier;
    final String emoji;

    RegistrationRoles(long roleId, String idenfier, String emoji) {
        this.roleId = roleId;
        this.identifier = idenfier;
        this.emoji = emoji;
    }

    public long id() { return this.roleId; }

    public String emoji() { return this.emoji; }
}