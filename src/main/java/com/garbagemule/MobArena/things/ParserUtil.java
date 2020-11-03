package com.garbagemule.MobArena.things;

import java.util.ArrayList;
import java.util.List;

class ParserUtil {

    static String extractBetween(String s, char left, char right) {
        int start = s.indexOf(left);
        if (start < 0) {
            throw new IllegalArgumentException("Missing start symbol " + left);
        }

        int end = s.lastIndexOf(right);
        if (end < 0) {
            throw new IllegalArgumentException("Missing end symbol " + right);
        }

        return s.substring(start + 1, end).trim();
    }

    static List<String> split(String s) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int parens = 0;
        int brackets = 0;
        int curlies = 0;
        int angles = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ',') {
                if (parens == 0 && brackets == 0 && curlies == 0 && angles == 0) {
                    String part = s.substring(start, i).trim();
                    if (!part.isEmpty()) {
                        result.add(part);
                    }
                    start = i + 1;
                }
            } else if (c == '(') {
                parens++;
            } else if (c == ')') {
                parens--;
                if (parens < 0) {
                    throw new IllegalArgumentException("Unmatched right parenthesis )");
                }
            } else if (c == '[') {
                brackets++;
            } else if (c == ']') {
                brackets--;
                if (brackets < 0) {
                    throw new IllegalArgumentException("Unmatched right square bracket ]");
                }
            } else if (c == '{') {
                curlies++;
            } else if (c == '}') {
                curlies--;
                if (curlies < 0) {
                    throw new IllegalArgumentException("Unmatched right curly brace }");
                }
            } else if (c == '<') {
                angles++;
            } else if (c == '>') {
                angles--;
                if (angles < 0) {
                    throw new IllegalArgumentException("Unmatched right angle bracket >");
                }
            }
        }
        if (parens > 0) {
            throw new IllegalArgumentException("Unmatched left parenthesis (");
        }
        if (brackets > 0) {
            throw new IllegalArgumentException("Unmatched left square bracket [");
        }
        if (curlies > 0) {
            throw new IllegalArgumentException("Unmatched left curly brace {");
        }
        if (angles > 0) {
            throw new IllegalArgumentException("Unmatched left angle bracket <");
        }
        if (start == 0) {
            String part = s.trim();
            if (!part.isEmpty()) {
                result.add(part);
            }
        } else {
            String part = s.substring(start).trim();
            if (!part.isEmpty()) {
                result.add(part);
            }
        }
        return result;
    }

}
