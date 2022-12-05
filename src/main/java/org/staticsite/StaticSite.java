package org.staticsite;

import org.staticsite.parser.MarkdownParser;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

@Command(
    name = "sitegen",
    description = "A simple static site generator",
    mixinStandardHelpOptions = true
)
public class StaticSite implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Provide verbose messages")
    boolean verbose;

    @Parameters(paramLabel="PROJECT_ROOT", description = "Path to project root directory")
    String rootDirPath;

    MarkdownParser parser = new MarkdownParser();

    @Override
    public void run() {
        parser.parse(rootDirPath);
        System.out.println("Site has been successfully generated!");
    }

    public static void main(String[] args) {

      new CommandLine(new StaticSite()).execute(args);
    }
}
