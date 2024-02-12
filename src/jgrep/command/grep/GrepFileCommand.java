package jgrep.command.grep;

import jgrep.command.Command;
import jgrep.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrepFileCommand extends Command<List<Hit>, Void> {
    private File targetFile;

    private String keyword;

    private String charsetName;

    private boolean isRegex;

    private boolean isIgnoreCase;

    private Pattern patternedKeyword;

    public List<Hit> processMain() throws IOException {
        if (isRegex) {
            return processRegexMain();
        }
        Function<String, Hit> processLine = isIgnoreCase ? this::processLineIgnoreCase : this::processLine;

        try {
            var lines = Files.readAllLines(this.targetFile.toPath(), Charset.forName(charsetName));
                return lines.stream()
                        .map(processLine)
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

    private Hit processLineIgnoreCase(String line) {
        if (StringUtils.containsIgnoreCase(line, keyword)) {
            return new Hit(targetFile, line);
        } else {
            return null;
        }
    }

    private List<Hit> processRegexMain() {
        int flags = 0;
        if (isIgnoreCase) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        patternedKeyword = Pattern.compile(keyword, flags);
        try {
            var lines = Files.readAllLines(this.targetFile.toPath(), Charset.forName(charsetName));
            return lines.stream()
                    .map(this::processRegexLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            return List.of();
        }
    }

    private Hit processRegexLine(String line) {
        var matcher = patternedKeyword.matcher(line);
        if (matcher.find()) {
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

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public boolean isIgnoreCase() {
        return isIgnoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        isIgnoreCase = ignoreCase;
    }
}
