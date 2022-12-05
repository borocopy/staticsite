package org.staticsite;

import org.apache.commons.io.FilenameUtils;
import org.staticsite.CLI.CLI;
import org.staticsite.compiler.Compiler;
import org.staticsite.parser.MarkdownParser;
import picocli.CommandLine;

public class StaticSite {
    private final MarkdownParser parser;
    private final Compiler compiler;
    private final CLI cli;
    private final CommandLine cl;
    private final Config config;

    public StaticSite(String[] args) {
        this.cli = new CLI();
        this.cl = new CommandLine(cli);
        cl.parseArgs(args);
        this.config = Config.init(cli.getConfigPath());
        this.parser = new MarkdownParser(config);
        this.compiler = new Compiler(config, parser, cli);
        this.cli.setCompiler(compiler);
    }

    public static void main(String[] args) {
        StaticSite staticSite = new StaticSite(args);
        staticSite.cl.execute(args);
    }
}
