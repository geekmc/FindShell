package org.sec;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

import java.util.ArrayList;
import java.util.List;

public class NameFilter implements ClassFilter {
    private String packageName;
    private static final List<String> blackList = new ArrayList<>();

    static {
        Constant.blackList.forEach(s -> blackList.add(s.split("#")[0]));
    }

    public NameFilter() {
    }

    public NameFilter(String packageName) {
        packageName = packageName.replace(".", "/");
        this.packageName = packageName;
    }

    @Override
    public boolean canInclude(InstanceKlass instanceKlass) {
        String klassName = instanceKlass.getName().asString();
        if (blackList.contains(klassName)) {
            return true;
        }
        if (packageName == null || packageName.equals("")) {
            for (String s : Constant.whiteList) {
                if (klassName.startsWith(s)) {
                    return false;
                }
            }
            return true;
        } else {
            return klassName.startsWith(packageName);
        }
    }
}
