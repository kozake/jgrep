package jgrep.command.grep;

import java.io.File;

public class GrepHit {
    private GrepHitType type;
    private File file;
    private String line;

    private int row;

    private int index;

    private Exception exception;

    private GrepHit() {
    }

    public static GrepHit hit(File file, String line, int row, int index) {
        GrepHit hit = new GrepHit();
        hit.type = GrepHitType.Hit;
        hit.file = file;
        hit.line = line;
        hit.row = row;
        hit.index = index;

        return hit;
    }

    public static GrepHit error(File file, Exception exception) {
        GrepHit hit = new GrepHit();
        hit.type = GrepHitType.Error;
        hit.file = file;
        hit.exception = exception;

        return hit;
    }

    public GrepHitType getType() {
        return type;
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

    public Exception getException() {
        return exception;
    }
}
