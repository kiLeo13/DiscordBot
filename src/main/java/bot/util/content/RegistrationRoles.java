package bot.util.content;

public enum RegistrationRoles {
    REQUIRED(740360659363168287L, "Registrador", "⚙"),

    REGISTERED(664923267601006622L, "Registrado", "✅"),
    NOT_REGISTERED(664921745777623088L, "Não registrado", "❌"),
    VERIFIED(758095502599520328L, "Verificado", "❌"),

    FEMALE(664916190082236427L, "Feminino", "✅"),
    MALE(664916190904320000L, "Masculino", "✅"),
    NON_BINARY(664916189029466122L, "Não binário", "✅"),

    ADULT(664918505963126814L, "Maior de idade", "✅"),
    UNDERAGE(664918505400958986L, "Menor de idade", "✅"),
    UNDER13(758095500884049960L, "😻", "✅"),

    PC(664917764229824512L, "Computador", "✅"),
    MOBILE(664917765395578892L, "Mobile", "✅");

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