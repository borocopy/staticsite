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
import java.util.*;
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
        String withAuthor = withDate.replaceFirst("\\{\\{\\s*author\s*}}", config.getAuthor());
        String html = withAuthor.replaceFirst("\\{\\{\\s*body\\s*}}", article.getContent());

        PrintWriter writer = new PrintWriter(
                Path.of(cli.getDistDirPath().toString(),
                        FilenameUtils.getBaseName(article.getFileName()) + ".html").toString(),
                StandardCharsets.UTF_8
        );
        writer.println(html);
        writer.close();
    }
    public void compileLanding() throws IOException, IncorrectArticleMetadataException, ParseException {
        StringBuffer listBuffer = new StringBuffer();
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o1.getDateWritten().compareTo(o2.getDateWritten());
            }
        });
        Collections.reverse(articles);
        for (Article article : articles) {
            String listItem = String.format(
                    "<li><a href=\"/%s\">%s</a><span class=\"article-date\">%s</span></li>",
                    article.getFileName(),
                    article.getTitle(),
                    dateParser.format(article.getDateWritten())
            );
           listBuffer.append(listItem + "\n");
        }

        String withTitle = landingTemplate.replaceAll("\\{\\{\\s*title\s*}}", config.getTitle());
        String html = withTitle.replaceFirst("\\{\\{\\s*links\\s*}}", listBuffer.toString());

        PrintWriter writer = new PrintWriter(
                Path.of(cli.getDistDirPath().toString(),"index.html").toString(),
                StandardCharsets.UTF_8
        );
        writer.println(html);
        writer.close();
    }
}
