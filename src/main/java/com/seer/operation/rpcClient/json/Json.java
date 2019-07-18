package com.seer.operation.rpcClient.json;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class Json {
    public Json() {

    }

    public static String stringify(Object o) {
        if (o == null) {
            return "null";
        } else if (!(o instanceof Number) && !(o instanceof Boolean)) {
            if (o instanceof Date) {
                return "new Date(" + ((Date) o).getTime() + ")";
            } else if (o instanceof Map) {
                return stringify((Map) o);
            } else if (o instanceof Iterable) {
                return stringify((Iterable) o);
            } else {
                return o instanceof Object[] ? stringify((Object[]) ((Object[]) o)) : stringify(String.valueOf(o));
            }
        } else {
            return String.valueOf(o);
        }
    }

    public static String stringify(Map m) {
        StringBuilder b = new StringBuilder();
        b.append('{');
        boolean first = true;
        Iterator var3 = m.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry e = (Map.Entry) var3.next();
            if (first) {
                first = false;
            } else {
                b.append(",");
            }

            b.append(stringify(e.getKey().toString()));
            b.append(':');
            b.append(stringify(e.getValue()));
        }

        b.append('}');
        return b.toString();
    }

    public static String stringify(Iterable c) {
        StringBuilder b = new StringBuilder();
        b.append('[');
        boolean first = true;

        Object o;
        for (Iterator var3 = c.iterator(); var3.hasNext(); b.append(stringify(o))) {
            o = var3.next();
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
        }

        b.append(']');
        return b.toString();
    }

    public static String stringify(Object[] c) {
        StringBuilder b = new StringBuilder();
        b.append('[');
        boolean first = true;
        Object[] var3 = c;
        int var4 = c.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Object o = var3[var5];
            if (first) {
                first = false;
            } else {
                b.append(",");
            }

            b.append(stringify(o));
        }

        b.append(']');
        return b.toString();
    }

    public static String stringify(String s) {
        StringBuilder b = new StringBuilder(s.length() + 2);
        b.append('"');

        for (; !s.isEmpty(); s = s.substring(1)) {
            char c = s.charAt(0);
            switch (c) {
                case '\b':
                    b.append("\\b");
                    break;
                case '\t':
                    b.append("\\t");
                    break;
                case '\n':
                    b.append("\\n");
                    break;
                case '\f':
                    b.append("\\f");
                    break;
                case '\r':
                    b.append("\\r");
                    break;
                case '"':
                case '\\':
                    b.append("\\");
                    b.append(c);
                    break;
                default:
                    b.append(c);
            }
        }

        b.append('"');
        return b.toString();
    }

    public static Object parse(String s) {
        return CrippledJavaScriptParser.parseJSExpr(s);
    }
}
