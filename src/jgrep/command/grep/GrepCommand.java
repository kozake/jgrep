package jgrep.command.grep;

import jgrep.command.Command;
import jgrep.command.event.CommandEvent;
import jgrep.command.event.CommandEventListener;
import jgrep.command.event.CommandEventType;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GrepCommand extends Command<List<GrepHit>, List<GrepHit>> {
    private File targetDirectory;

    private String targetGlobPattern;

    private String keyword;

    private String charsetName;

    private boolean isRegex;

    private boolean isIgnoreCase;

    private int threads;

    private ExecutorService executorService;

    public List<GrepHit> processMain() throws Exception {
        executorService = Executors.newFixedThreadPool(threads);
        try {
            return grep(targetDirectory);
        }finally {
            executorService.shutdown();
        }
    }

    private List<Future<List<GrepHit>>> grep(
            final List<Future<List<GrepHit>>> acc,
            final CommandEventListener<List<GrepHit>, Void> commandEventListener,
            final File file,
            final PathMatcher matcher) {

        try (Stream<Path> stream = Files.walk(file.toPath())) {
            stream.filter(matcher::matches).forEach(path -> {
                if (Thread.currentThread().isInterrupted()) {
                    throw new RuntimeException("canceled");
                }
                if (!Files.isDirectory(path)) {
                    Future<List<GrepHit>> future = executorService.submit(() -> {
                        GrepFileCommand command = new GrepFileCommand();
                        command.setTargetFile(path.toFile());
                        command.setKeyword(keyword);
                        command.setCharsetName(charsetName);
                        command.setRegex(isRegex);
                        command.setIgnoreCase(isIgnoreCase);
                        command.addCommandEventListener(commandEventListener);
                        return command.execute();
                    });
                    acc.add(future);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return acc;
    }

    private List<GrepHit> grep(final File file) {
        AtomicInteger countTarget = new AtomicInteger(CommandEvent.UNKNOWN_PROGRESS);
        CommandEventListener<List<GrepHit>, Void> commandEventListener = new CommandEventListener<List<GrepHit>, Void>() {
            final AtomicInteger finished = new AtomicInteger(0);
            @Override
            public void actionPerformed(CommandEvent<List<GrepHit>, Void> event) {
                if (event.getType() == CommandEventType.Finish) {
                    List<GrepHit> result = event.getResult();
                    List<GrepHit>[] resultArr = new List[]{result};
                    fireCommandEvent(CommandEvent.newProcess(
                            finished.incrementAndGet(),
                            countTarget.get(),
                            resultArr));
                }
            }
        };

        FileSystem fileSystem = FileSystems.getDefault();
        PathMatcher matcher = fileSystem.getPathMatcher(
                "glob:**/{" + (targetGlobPattern.isEmpty() ? "*" : targetGlobPattern) + "}");

        List<Future<List<GrepHit>>> futureResults = grep(new ArrayList<>(), commandEventListener, file, matcher);
        countTarget.set(futureResults.size());

        List<GrepHit> result = new ArrayList<>();
        for (Future<List<GrepHit>> futureResult : futureResults) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("canceld");
                break;
            }
            try {
                result.addAll(futureResult.get());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return result;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getTargetGlobPattern() {
        return targetGlobPattern;
    }

    public void setTargetGlobPattern(String targetGlobPattern) {
        this.targetGlobPattern = targetGlobPattern;
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

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
