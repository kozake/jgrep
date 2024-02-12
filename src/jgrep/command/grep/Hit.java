package jgrep.command.grep;

import java.io.File;

public class Hit {
    private File file;
    private String line;

    public Hit(File file, String line) {
        this.file = file;
        this.line = line;
    }

    public File getFile() {
        return file;
    }

    public String getLine() {
        return line;
    }
}
