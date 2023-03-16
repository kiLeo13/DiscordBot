package bot.util;

import java.util.List;

public enum Channels {
    COMMAND_PING_CHANNELS(List.of(1085377131019763802L)),
    REGISTER_FILTER_CHANNELS(List.of(1085377131019763802L)),
    REGISTER_CHANNELS(List.of(1085377131019763802L)),
    COUNTDOWN_CHANNELS(List.of(1085377131019763802L)),
    SWEARING_CHANNELS(List.of(1085377131019763802L)),
    STICKERS_CHANNELS(List.of(1085377131019763802L)),
    DISCONNECT_CHANNELS(List.of(1085377131019763802L));

    final List<Long> list;

    Channels(List<Long> list) {
        this.list = list;
    }

    public List<Long> get() { return this.list; }
}