package jgrep.command.grep;

import java.io.File;

public class Hit {
    private File file;
    private String line;

    private int row;

    private int index;

    public Hit(File file, String line, int row, int index) {
        this.file = file;
        this.line = line;
        this.row = row;
        this.index = index;
    }

    public File getFile() {
        return file;
    }

    public String getLine() {
        return line;
    }

    public int getRow() {
        return row;
    }

    public int getIndex() {
        return index;
    }
}
