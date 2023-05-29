package bot.util.economy;

import java.util.Collections;
import java.util.List;

public class Item {
    private final String id;
    private final String name;
    private final String description;
    private final boolean isInventory;
    private final boolean isUsable;
    private final boolean isSellable;
    private final boolean isLimited;
    private final int stockRemaining;
    private final List<Action> actions;
    private final List<Requirement> requirements;

    public Item(
            String id,
            String name,
            String description,
            boolean is_inventory,
            boolean is_usable,
            boolean is_sellable,
            boolean unlimited_stock,
            int stock_remaining,
            List<Action> actions,
            List<Requirement> requirements
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isInventory = is_inventory;
        this.isUsable = is_usable;
        this.isSellable = is_sellable;
        this.isLimited = !unlimited_stock;
        this.stockRemaining = stock_remaining;
        this.actions = Collections.unmodifiableList(actions);
        this.requirements = Collections.unmodifiableList(requirements);
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }

    public boolean isInventory() {
        return this.isInventory;
    }

    public boolean isUsable() {
        return this.isUsable;
    }

    public boolean isSellable() {
        return this.isSellable;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public int stockRemaining() {
        return this.stockRemaining;
    }

    public List<Action> actions() {
        return this.actions;
    }

    public List<Requirement> requirements() {
        return this.requirements;
    }
}