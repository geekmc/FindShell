package org.sec;

import com.beust.jcommander.Parameter;

public class Command {
    @Parameter(names = {"-h", "--help"}, description = "Help Info", help = true)
    public boolean help;

    @Parameter(names = {"-p","--pid"}, description = "Target JVM pid")
    public String pid;

    @Parameter(names = {"--package"}, description = "Target Package")
    public String packageName;

    @Parameter(names = {"--debug"}, description = "Use Debug Module")
    public boolean debug;
}
