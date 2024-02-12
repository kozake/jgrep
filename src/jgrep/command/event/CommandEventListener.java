package jgrep.command.event;

public interface CommandEventListener<T, V> {

    void actionPerformed(CommandEvent<T, V> e);
}
