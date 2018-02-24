package cf.wynntils.core.framework.enums;

public enum Priority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;

    public static Priority valueOf(int i) {
        return Priority.values()[i];
    }
}
