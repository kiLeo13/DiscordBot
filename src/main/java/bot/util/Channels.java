package bot.util;

import java.util.List;

public enum Channels {
    REGISTER_CHANNEL(757408993164394527L),
    REGISTER_AGE_FILTER_CHANNELS(List.of(757408993164394527L)),
    REGISTER_LOG_CHANNEL(664972939908743209L),

    COMMAND_DISCONNECT_CHANNELS(List.of(648416180834402305L,739881973140554382L,735332191579668570L)),
    COMMAND_COLOR_ROLE_CHANNELS(List.of()), // Empty for now
    COMMAND_PUTA_CHANNELS(List.of(735332191579668570L,739881973140554382L,1061521840939483186L,904889532632141854L)),

    STAFF_AJUDANTES_CHANNEL(1061526121209938000L),

    LOG_COLOR_ROLE_COMMAND_CHANNEL(948340403806031923L);

    List<Long> ids;
    long id;

    Channels(long id) { this.id = id; }
    Channels(List<Long> ids) { this.ids = ids; }

    public long toId() { return this.id; }
    public List<Long> toIds() { return this.ids; }
}