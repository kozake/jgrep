package jgrep.command;

import jgrep.command.event.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Command<T, V> {

    private final List<CommandEventListener<T, V>> commandEventListeners = new ArrayList<>();

    public void addCommandEventListener(final CommandEventListener<T, V> listener) {
        commandEventListeners.add(listener);
    }

    public void removeCommandEventListener(final CommandEventListener<T, V> listener) {
        commandEventListeners.remove(listener);
    }

    protected void fireCommandEvent(final CommandEvent<T, V> event) {
        List<CommandEventListener<T, V>> listeners = new ArrayList<>(commandEventListeners);
        for (CommandEventListener<T, V> listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    public T execute() throws Exception {
        fireCommandEvent(CommandEvent.newStart());
        T result = processMain();
        fireCommandEvent(CommandEvent.newFinish(result));
        return result;
    }

    public abstract T processMain() throws Exception;
}
