package org.staticsite.compiler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.staticsite.CLI.CLI;
import org.staticsite.Config;
import org.staticsite.entity.Article;
import org.staticsite.entity.IncorrectArticleMetadataException;
import org.staticsite.parser.MarkdownParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Compiler {

    private final Config config;
    private final MarkdownParser parser;
    private final CLI cli;
    private String articleTemplate;
    private String landingTemplate;
    private List<Article> articles = new ArrayList<Article>();

    public Compiler(Config config, MarkdownParser mdParser, CLI cli) {
        this.config = config;
        this.parser = mdParser;
        this.cli = cli;
        try (FileInputStream is = new FileInputStream(Path.of(
                cli.getSrcDirPath().toString(),
                "layouts/article.html"
        ).toFile())) {
            this.articleTemplate = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Article layout not found!");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try (FileInputStream is = new FileInputStream(Path.of(
                cli.getSrcDirPath().toString(),
                "layouts/landing.html"
        ).toFile())) {
            this.landingTemplate = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Landing layout not found!");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void compileArticle(File articleFile) throws IOException, IncorrectArticleMetadataException, ParseException {
        Article article = parser.parseArticle(articleFile);
        this.articles.add(article);
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");

        String withTitle = articleTemplate.replaceFirst("\\{\\{\\s*title\s*}}", article.getTitle());
        String withDate = withTitle
                .replaceFirst(
                        "\\{\\{\\s*date\\s*}}",
                        dateParser.format(article.getDateWritten())
                );
        String html = withDate.replaceFirst("\\{\\{\\s*body\\s*}}", article.getContent());

        PrintWriter writer = new PrintWriter(
                Path.of(cli.getDistDirPath().toString(),
                        FilenameUtils.getBaseName(article.getFileName()) + ".html").toString(),
                StandardCharsets.UTF_8
        );
        writer.println(html);
        writer.close();
    }
}
