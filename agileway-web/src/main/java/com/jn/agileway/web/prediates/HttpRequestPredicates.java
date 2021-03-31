package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.util.Collection;
import java.util.List;

public class HttpRequestPredicates {

    /**
     *
     * @param o
     * @param specIfString 如果参数 o 是String，则用 该参数作为分隔符
     * @return 处理后的字符串列表
     */
    public static List<String> toStringList(Object o, String specIfString) {
        if (Objs.isEmpty(o)) {
            return Collects.unmodifiableArrayList();
        }
        specIfString = Objs.useValueIfNull(specIfString, ", ");

        if (o instanceof String) {
            return Collects.asList(Strings.split((String) o, specIfString));
        }

        if (Arrs.isArray(o) || o instanceof Collection) {
            List<String> ret = Pipeline.of(Collects.asIterable(o)).filter(new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return o instanceof String;
                }
            }).map(Functions.toStringFunction()).asList();
            return ret;
        }
        return Collects.unmodifiableArrayList();
    }
}
