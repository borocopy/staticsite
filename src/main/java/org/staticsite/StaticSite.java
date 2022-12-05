package org.staticsite;

import org.apache.commons.io.FilenameUtils;
import org.staticsite.parser.MarkdownParser;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import javax.annotation.processing.Filer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            List<File> filesInFolder = Files.walk(Paths.get(rootDirPath))
                    .filter(Files::isRegularFile)
                    .filter(
                        (path) -> FilenameUtils.getExtension(path.toString()).equals("md")
                    )
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            for (File file: filesInFolder) {
                parser.parse(file.getAbsolutePath());
                System.out.println(
                    "File " + file.getName() + " has been processed."
                );
            }
            System.out.println("Site has been successfully generated!");
        } catch(IOException error) {
           System.err.println(error.getMessage());
        }
    }

    public static void main(String[] args) {

      new CommandLine(new StaticSite()).execute(args);
    }
}
