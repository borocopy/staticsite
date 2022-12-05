package org.staticsite.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    private String inputDir;
    private String outputDir;

    public MarkdownParser() {}

    public Block blockFromBuffer(StringBuffer buffer) {
        String content = buffer.toString().trim();
        Pattern headingPattern = Pattern.compile("^(#+)\\S+(.*)$");
        Matcher headingMatcher = headingPattern.matcher(content);
        Block block;

        if (headingMatcher.find()) {
            block = new Block.BlockBuilder(
                    String.format("h%d", headingMatcher.group(1).length())
                )
                .setContent(headingMatcher.group(2))
                .build();
        } else {
            block = new Block.BlockBuilder("p").setContent(content).build();
        }

        return block;
    }

    public void parse(String pathToFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
           String line;
           StringBuffer buffer = new StringBuffer();
           while ((line = reader.readLine()) != null) {
               buffer.append(" " + line);
               if (line.isBlank()) {
                   System.out.println(
                       blockFromBuffer(buffer).toString()
                   );
                   buffer.setLength(0);
               }
           }
            System.out.println(
                blockFromBuffer(buffer).toString()
            );
        } catch(Exception err) {

        }
    }
}
