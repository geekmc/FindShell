package org.sec;

public class Result {
    private String key;
    private Integer type;

    public static final int RUNTIME_EXEC_TIME = 1;
    public static final int CLASSLOADER_DEFINE = 2;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeWord() {
        switch (this.type) {
            case RUNTIME_EXEC_TIME:
                return "Runtime-Exec";
            case CLASSLOADER_DEFINE:
                return "ClassLoader-DefineClass";
            default:
                return "";
        }
    }
}
