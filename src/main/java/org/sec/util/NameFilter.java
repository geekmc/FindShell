package org.sec.util;

import org.sec.Constant;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

import java.util.ArrayList;
import java.util.List;

public class NameFilter implements ClassFilter {
    private static final List<String> blackList = new ArrayList<>();

    static {
        Constant.blackList.forEach(s -> blackList.add(s.split("#")[0]));
    }

    public NameFilter() {
    }

    @Override
    public boolean canInclude(InstanceKlass instanceKlass) {
        String klassName = instanceKlass.getName().asString();
        if (blackList.contains(klassName)) {
            return true;
        }
        for (String k : Constant.keyword) {
            if (klassName.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
