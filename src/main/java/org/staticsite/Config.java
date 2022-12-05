package org.staticsite;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class Config {

    private String title;
    private String author;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Config{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    public static Config init(Path configPath) {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        try (FileInputStream is = new FileInputStream(configPath.toFile())) {

            Config cfg = yaml.load(is);

            return cfg;
        } catch(IOException error) {
            System.err.println("Unable to read config file.");
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return null;
    }
}
