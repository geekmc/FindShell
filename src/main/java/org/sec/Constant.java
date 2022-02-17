package org.sec;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    // BLACKLIST (Analysis Target)
    // CLASS_NAME#METHOD_NAME
    public static List<String> blackList = new ArrayList<>();
    // WHITELIST (Safety)
    public static List<String> whiteList = new ArrayList<>();
    // SHELL KEYWORD
    public static List<String> keyword = new ArrayList<>();

    static {
        blackList.add("javax/servlet/http/HttpServlet#service");
        blackList.add("org/apache/catalina/core/ApplicationFilterChain#doFilter");
        blackList.add("org/springframework/web/servlet/DispatcherServlet#doService");
        blackList.add("org/apache/tomcat/websocket/server/WsFilter#doFilter");

        whiteList.add("org/apache/");
        whiteList.add("org/slf4j/");
        whiteList.add("org/thymeleaf/");
        whiteList.add("org/springframework/");
        whiteList.add("org/aopalliance/");
        whiteList.add("org/attoparser/");
        whiteList.add("org/omg/");
        whiteList.add("org/xml/");
        whiteList.add("org/eclipse/");
        whiteList.add("org/w3c/");
        whiteList.add("org/ietf/");
        whiteList.add("org/mybatis/");
        whiteList.add("org/hibernate/");
        whiteList.add("org/ow2/");
        whiteList.add("org/jcp/");
        whiteList.add("ch/qos/");
        whiteList.add("com/alibaba/");
        whiteList.add("com/sun/");
        whiteList.add("com/fasterxml/");
        whiteList.add("com/intellij/");
        whiteList.add("java/");
        whiteList.add("javax/");
        whiteList.add("jdk/");
        whiteList.add("sun/");

        keyword.add("shell");
        keyword.add("memshell");
        keyword.add("agentshell");
        keyword.add("exploit");
        keyword.add("payload");
        keyword.add("exp");
        keyword.add("poc");
    }
}
