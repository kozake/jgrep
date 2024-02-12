package jgrep.command.grep;

import jgrep.command.Command;
import jgrep.command.event.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrepCommand extends Command<List<Hit>, List<Hit>> {
    private File targetDirectory;

    private String keyword;

    private String charsetName;

    private boolean isRegex;

    private int threads;

    private ExecutorService executorService;

    public List<Hit> processMain() throws Exception {
        executorService = Executors.newFixedThreadPool(threads);
        try {
            return grep(targetDirectory);
        }finally {
            executorService.shutdown();
        }
    }

    private List<Future<List<Hit>>> grep(
            final List<Future<List<Hit>>> acc,
            final CommandEventListener<List<Hit>, Void> commandEventListener,
            final File file) {

        if (Thread.currentThread().isInterrupted()) {
            return Collections.emptyList();
        }

        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                grep(acc, commandEventListener, f);
            }
        } else {
            var future = executorService.submit(() -> {
                var command = new GrepFileCommand();
                command.setTargetFile(file);
                command.setKeyword(keyword);
                command.setCharsetName(charsetName);
                command.setRegex(isRegex);
                command.addCommandEventListener(commandEventListener);
                return command.execute();
            });
            acc.add(future);
        }
        return acc;
    }

    private List<Hit> grep(final File file) {
        AtomicInteger countTarget = new AtomicInteger(CommandEvent.UNKNOWN_PROGRESS);
        var commandEventListener = new CommandEventListener<List<Hit>, Void>() {
            final AtomicInteger finished = new AtomicInteger(0);
            @Override
            public void actionPerformed(CommandEvent<List<Hit>, Void> event) {
                if (event.getType() == CommandEventType.Finish) {
                    var result = event.getResult();
                    List<Hit>[] resultArr = new List[]{result};
                    fireCommandEvent(CommandEvent.newProcess(
                            finished.incrementAndGet(),
                            countTarget.get(),
                            resultArr));
                }
            }
        };

        var futureResults = grep(new ArrayList<>(), commandEventListener, file);
        countTarget.set(futureResults.size());

        List<Hit> result = new ArrayList<>();
        for (Future<List<Hit>> futureResult : futureResults) {
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

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
