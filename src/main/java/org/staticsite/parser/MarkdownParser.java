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

    public StringBuffer parse(String pathToFile) {
        StringBuffer parsedBuffer = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
           String line;
           StringBuffer buffer = new StringBuffer();
           while ((line = reader.readLine()) != null) {
               buffer.append(" " + line);
               if (line.isBlank()) {
                   parsedBuffer.append(blockFromBuffer(buffer));
                   parsedBuffer.append("\n");
                   buffer.setLength(0);
               }
           }
            parsedBuffer.append(blockFromBuffer(buffer));
        } catch(Exception err) {

        }

        return parsedBuffer;
    }
}
