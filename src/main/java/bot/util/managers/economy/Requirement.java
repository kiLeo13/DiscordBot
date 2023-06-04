package bot.util.managers.economy;

import java.util.Collections;
import java.util.List;

class Requirement {
    private final int type;
    private final int matchType;
    private final List<String> ids;

    public Requirement(int type, int match_type, List<String> ids) {
        this.type = type;
        this.matchType = match_type;
        this.ids = Collections.unmodifiableList(ids);
    }

    public int type() {
        return this.type;
    }

    public int matchType() {
        return this.matchType;
    }

    public List<String> ids() {
        return this.ids;
    }
}