package org.staticsite.parser;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String tag;
    private Boolean isSingular;
    private String content;
    private List<String> classes;

    @Override
    public String toString() {
        String classString = String.join(" ", classes);
        StringBuffer buffer = new StringBuffer("");
        if (isSingular) {
            buffer.append(
                    String.format("<%s ", tag)
            );
            if (!classes.isEmpty()) {
                buffer.append(
                        String.format("class=\"%s\" ", classString)
                );
            }
            buffer.append("/>");
        } else {
            buffer.append(
                    String.format("<%s", tag)
            );
            if (!classes.isEmpty()) {
                buffer.append(
                        String.format(" class=\"%s\" ", classString)
                );
            }
            buffer.append(
                    String.format(">%s</%s>", content, tag)
            );
        }

        return buffer.toString();
    }

    private Block(BlockBuilder blockBuilder) {
        tag = blockBuilder.tag;
        isSingular = blockBuilder.isSingular;
        content = blockBuilder.content;
        classes = blockBuilder.classes;
    }

    public String getTag() {
        return tag;
    }

    public Boolean getSingular() {
        return isSingular;
    }

    public String getContent() {
        return content;
    }

    public List<String> getClasses() {
        return classes;
    }

    public static class BlockBuilder {
        private String tag;
        private Boolean isSingular;
        private String content;
        private List<String> classes;



        public BlockBuilder(String tag) {
            this.tag = tag;
            this.isSingular = false;
            this.classes = new ArrayList<String>();
        }

        public BlockBuilder setIsSingular(Boolean isSingular) {
            this.isSingular = isSingular;
            return this;
        }

        public BlockBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public BlockBuilder addClass(String content) {
            this.classes.add(content);
            return this;
        }

        public Block build() {
            return new Block(this);
        }
    }
}
