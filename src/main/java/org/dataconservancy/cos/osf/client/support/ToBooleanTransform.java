package org.dataconservancy.cos.osf.client.support;

import java.util.function.Function;

/**
 * Created by esm on 6/3/16.
 */
public class ToBooleanTransform implements Function<String, Boolean> {
    @Override
    public Boolean apply(String s) {
        return Boolean.parseBoolean(s);
    }
}
