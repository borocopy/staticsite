package org.staticsite;

import org.apache.commons.io.FilenameUtils;
import org.staticsite.parser.MarkdownParser;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Parameters(paramLabel = "PROJECT_ROOT", description = "Path to project root directory")
    String rootDirPath;
    private MarkdownParser parser = new MarkdownParser();
    public static Config config;

    public static void main(String[] args) {
        StaticSite staticSite = new StaticSite();
        CommandLine cl = new CommandLine(staticSite);
        cl.parseArgs(args);
        config = Config.init(staticSite.getConfigPath());
        cl.execute(args);
    }

    private Path getSrcDirPath() {
        return Path.of(rootDirPath, "src").toAbsolutePath();
    }

    private Path getDistDirPath() {
        return Path.of(rootDirPath, "dist").toAbsolutePath();
    }

    private Path getConfigPath() {
        return Path.of(rootDirPath, "config.yaml").toAbsolutePath();
    }

    @Override
    public void run() {
        try {
            List<File> filesInFolder = Files.walk(getSrcDirPath())
                    .filter(Files::isRegularFile)
                    .filter(
                            (path) -> FilenameUtils.getExtension(path.toString()).equals("md")
                    )
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            for (File file : filesInFolder) {
                StringBuffer parsedHTML = parser.parse(file.getAbsolutePath());
                PrintWriter writer = new PrintWriter(
                        Path.of(getDistDirPath().toString(),
                                FilenameUtils.getBaseName(file.getPath()) + ".html").toString(),
                        StandardCharsets.UTF_8
                );
                writer.println(parsedHTML);
                writer.close();
                System.out.println(
                        "File " + file.getName() + " has been processed."
                );
            }
            System.out.println("Site has been successfully generated!");
        } catch (IOException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}
