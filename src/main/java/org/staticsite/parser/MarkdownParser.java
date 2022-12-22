package org.staticsite.parser;

import org.staticsite.Config;
import org.staticsite.entity.Article;
import org.staticsite.entity.IncorrectArticleMetadataException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    private String inputDir;
    private String outputDir;
    private final Config config;

    public MarkdownParser(Config config) {
        this.config = config;
    }

    public Block blockFromBuffer(StringBuffer buffer) {
        String content = buffer.toString().trim();
        Pattern headingPattern = Pattern.compile("^(#+)\\s+(.*)$");
        Matcher headingMatcher = headingPattern.matcher(content);
        Block block;

        if (headingMatcher.find()) {
            Integer headingValue =
                    headingMatcher.group(1).length() > 6 ? 6 : headingMatcher.group(1).length();
            block = new Block.BlockBuilder(
                    String.format("h%d", headingValue)
                )
                .setContent(headingMatcher.group(2))
                .build();
        } else {
            block = new Block.BlockBuilder("p").setContent(content).build();
        }

        return block;
    }

    public Article parseArticle(File pathToFile) throws
            IncorrectArticleMetadataException,
            IOException,
            ParseException
    {
        StringBuffer parsedBuffer = new StringBuffer();
        HashMap<String, String> articleInfo = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
           String line;
           StringBuffer buffer = new StringBuffer();

           Pattern keyValuePattern = Pattern.compile("^(.+):(.+)$");
           while (!(line = reader.readLine()).trim().toUpperCase().equals("BEGIN")) {
               Matcher keyValueMatcher = keyValuePattern.matcher(line);
               if (keyValueMatcher.find()) {
                   articleInfo.put(keyValueMatcher.group(1), keyValueMatcher.group(2));
               } else {
                   throw new IncorrectArticleMetadataException(
                       String.format(
                               "Incorrect key/value pair: %s %s",
                               keyValueMatcher.group(1).trim(),
                               keyValueMatcher.group(2).trim()
                       )
                   );
               }
               buffer.setLength(0);
           }

           while ((line = reader.readLine()) != null) {
               buffer.append(" " + line);
               if (line.isBlank()) {
                   parsedBuffer.append(blockFromBuffer(buffer));
                   parsedBuffer.append("\n");
                   buffer.setLength(0);
               }
           }
            parsedBuffer.append(blockFromBuffer(buffer));
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        return new Article(
                config.getAuthor(),
                articleInfo.get("title"),
                dateParser.parse(articleInfo.get("date")),
                parsedBuffer.toString()
        );
    }
}
