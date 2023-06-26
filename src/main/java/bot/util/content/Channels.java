package bot.util.content;

import java.util.List;

public enum Channels {
    REGISTER_CHANNEL("757408993164394527"),
    AGE_FILTER_CHANNELS("757408993164394527"),
    REGISTER_LOG_CHANNEL("664972939908743209"),
    STAFF_AJUDANTES_CHANNEL("1061526121209938000"),
    LOG_CLOSED_TICKETS("1114759342319743046"),
    SALADA("735332191579668570"),
    COMANDOS_SALADA("739881973140554382"),
    CHANNEL_BANK("599685849487245322");

    final String[] ids;

    Channels(String... ids) {
        this.ids = ids;
    }

    public String id() {
        return this.ids.length == 0
                ? ""
                : this.ids[0];
    }

    public List<String> ids() {
        return List.of(this.ids);
    }
}