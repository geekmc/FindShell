package org.sec;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    // BLACKLIST (Analysis Target)
    // CLASS_NAME#METHOD_NAME
    public static List<String> blackList = new ArrayList<>();
    // SHELL KEYWORD
    public static List<String> keyword = new ArrayList<>();

    static {
        blackList.add("javax/servlet/http/HttpServlet#service");
        blackList.add("org/apache/catalina/core/ApplicationFilterChain#doFilter");
        blackList.add("org/springframework/web/servlet/DispatcherServlet#doService");
        blackList.add("org/apache/tomcat/websocket/server/WsFilter#doFilter");

        keyword.add("shell");
        keyword.add("memshell");
        keyword.add("agentshell");
        keyword.add("exploit");
        keyword.add("payload");
        keyword.add("rebeyond");
        keyword.add("metasploit");
    }
}
