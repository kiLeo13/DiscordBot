package bot.util.content;

import java.util.Arrays;
import java.util.List;

public enum Channels {
    REGISTER_CHANNEL("757408993164394527"),
    REGISTER_AGE_FILTER_CHANNELS("757408993164394527"),
    REGISTER_LOG_CHANNEL("664972939908743209"),
    COMMAND_DISCONNECT_SELF_CHANNELS("648416180834402305","739881973140554382","735332191579668570"),
    STAFF_AJUDANTES_CHANNEL("1061526121209938000"),
    LOG_COLOR_ROLE_COMMAND_CHANNEL("948340403806031923"),
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