package jgrep.command.event;

public class CommandEvent<T, V> {
    public static int UNKNOWN_PROGRESS = -1;
    private CommandEventType type;
    private V[] chunks;
    private int top = UNKNOWN_PROGRESS;
    private int bottom = UNKNOWN_PROGRESS;
    private T result;

    private CommandEvent(CommandEventType type) {
        this.type = type;
    }

    public CommandEventType getType() {
        return type;
    }

    public V[] getChunks() {
        return chunks;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getProgress() {
        if (top == UNKNOWN_PROGRESS || bottom == UNKNOWN_PROGRESS) {
            return UNKNOWN_PROGRESS;
        }
        return top * 100 / bottom;
    }

    public T getResult() {
        return result;
    }

    public static <T, V>CommandEvent<T, V> newStart() {
        CommandEvent<T, V> event = new CommandEvent<T, V>(CommandEventType.Start);
        return event;
    }

    public static <T, V> CommandEvent<T, V> newProcess(int top, int bottom, V... chunks) {
        CommandEvent<T, V> event = new CommandEvent<T, V>(CommandEventType.Process);
        event.top = top;
        event.bottom = bottom;
        event.chunks = chunks;
        return event;
    }

    public static <T, V> CommandEvent<T, V> newFinish(T result) {
        CommandEvent<T, V> event = new CommandEvent<T, V>(CommandEventType.Finish);
        event.result = result;
        return event;
    }
}
