package org.staticsite.CLI;

import org.apache.commons.io.FilenameUtils;
import org.staticsite.compiler.Compiler;
import org.staticsite.entity.IncorrectArticleMetadataException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Command(
        name = "sitegen",
        description = "A simple static site generator",
        mixinStandardHelpOptions = true
)
public class CLI implements Runnable {
    @Option(names = {"-v", "--verbose"}, description = "Provide verbose messages")
    boolean verbose;

    @Parameters(paramLabel = "PROJECT_ROOT", description = "Path to project root directory")
    String rootDirPath;

    public String getRootDirPath() {
        return rootDirPath;
    }

    public Path getSrcDirPath() {
        return Path.of(rootDirPath, "src").toAbsolutePath();
    }

    public Path getDistDirPath() {
        return Path.of(rootDirPath, "dist").toAbsolutePath();
    }

    public Path getConfigPath() {
        return Path.of(rootDirPath, "config.yaml").toAbsolutePath();
    }

    private Compiler compiler;

    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
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
                try {
                    compiler.compileArticle(file);
                } catch (IncorrectArticleMetadataException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
                System.out.println(
                        "File " + file.getName() + " has been processed."
                );
            }
            compiler.compileLanding();
            System.out.println("Site has been successfully generated!");
        } catch (IOException | IncorrectArticleMetadataException | ParseException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}
