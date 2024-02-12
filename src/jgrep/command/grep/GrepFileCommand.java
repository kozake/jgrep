package jgrep.command.grep;

import jgrep.command.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GrepFileCommand extends Command<List<Hit>, Void> {
    private File targetFile;

    private String keyword;

    public List<Hit> processMain() throws IOException {
        try {
            var lines = Files.readAllLines(this.targetFile.toPath());
                return lines.stream()
                        .map(this::processLine)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        } catch (IOException ex) {
            return List.of();
        }
    }

    private Hit processLine(String line) {
        if (line.contains(keyword)) {
            return new Hit(targetFile, line);
        } else {
            return null;
        }
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
