package jgrep.command.grep;

import jgrep.command.Command;
import jgrep.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrepFileCommand extends Command<List<Hit>, Void> {
    private File targetFile;

    private String keyword;

    private String charsetName;

    private boolean isRegex;

    private boolean isIgnoreCase;

    private Pattern patternedKeyword;

    private int row = 0;

    public List<Hit> processMain() throws IOException {
        if (isRegex) {
            return processRegexMain();
        }
        Function<String, Hit> processLine = isIgnoreCase ? this::processLineIgnoreCase : this::processLine;

        try {
            List<String> lines = Files.readAllLines(this.targetFile.toPath(), Charset.forName(charsetName));
                return lines.stream()
                        .peek(line -> row++)
                        .map(processLine)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

    private Hit processLine(String line) {
        int findIndex = line.indexOf(keyword);
        if (findIndex != -1) {
            return new Hit(targetFile, line, row, findIndex);
        } else {
            return null;
        }
    }

    private Hit processLineIgnoreCase(String line) {
        int findIndex = StringUtils.indexOfIgnoreCase(line, keyword);
        if (findIndex != -1) {
            return new Hit(targetFile, line, row, findIndex);
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
            List<String> lines = Files.readAllLines(this.targetFile.toPath(), Charset.forName(charsetName));
            return lines.stream()
                    .peek(line -> row++)
                    .map(this::processRegexLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

    private Hit processRegexLine(String line) {
        Matcher matcher = patternedKeyword.matcher(line);
        if (matcher.find()) {
            return new Hit(targetFile, line, row, matcher.start());
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
