package com.garbagemule.MobArena.things;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class ParserUtilTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void extractBetweenThrowsIfNoLeftSymbol() {
        String input = "a, b, c)";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.extractBetween(input, '(', ')');
    }

    @Test
    public void extractBetweenThrowsIfNoRightSymbol() {
        String input = "(a, b, c";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.extractBetween(input, '(', ')');
    }

    @Test
    public void extractBetweenStripsPrefixAndSuffix() {
        String inner = "a, b, c";
        String input = "hello [" + inner + "] world";

        String result = ParserUtil.extractBetween(input, '[', ']');

        assertThat(result, equalTo(inner));
    }

    @Test
    public void extractBetweenSkipsNestedGroups() {
        String inner = "a, b, c";
        String wrapped = "<" + inner + ">";
        String input = "<" + wrapped + ">";

        String result = ParserUtil.extractBetween(input, '<', '>');

        assertThat(result, equalTo(wrapped));
    }

    @Test
    public void extractBetweenThingGroup1() {
        String inner = "random(a, b), random(c, d)";
        String input = "all(" + inner + ")";

        String result = ParserUtil.extractBetween(input, '(', ')');

        assertThat(result, equalTo(inner));
    }

    @Test
    public void splitThrowsOnUnmatchedLeftParen() {
        String input = "(a, b), (c, d";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedRightParen() {
        String input = "(a, b), c, d)";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedLeftBracket() {
        String input = "[a, b], [c, d";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedRightBracket() {
        String input = "[a, b], c, d]";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedLeftBrace() {
        String input = "{a, b}, {c, d";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedRightBrace() {
        String input = "{a, b}, c, d}";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedLeftAngle() {
        String input = "<a, b>, <c, d";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitThrowsOnUnmatchedRightAngle() {
        String input = "<a, b>, c, d>";
        exception.expect(IllegalArgumentException.class);

        ParserUtil.split(input);
    }

    @Test
    public void splitReturnsEmptyListOnEmptyInput() {
        String input = "   ";

        List<String> result = ParserUtil.split(input);

        assertThat(result, empty());
    }

    @Test
    public void splitReturnsInputIfNoCommas() {
        String input = "abc";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Collections.singletonList(input)));
    }

    @Test
    public void splitWorksOnBareLists() {
        String input = "abc, de, f";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "de", "f")));
    }

    @Test
    public void splitOmitsEmptyParts() {
        String input = "abc, , f, ";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "f")));
    }

    @Test
    public void splitSkipsParentheses() {
        String input = "abc, (de, f)";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "(de, f)")));
    }

    @Test
    public void splitSkipsSquareBrackets() {
        String input = "abc, [de, f]";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "[de, f]")));
    }

    @Test
    public void splitSkipsCurlyBraces() {
        String input = "abc, {de, f}";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "{de, f}")));
    }

    @Test
    public void splitSkipsAngleBrackets() {
        String input = "abc, <de, f>";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList("abc", "<de, f>")));
    }

    @Test
    public void splitThingGroup1() {
        String input = "all(random(a, b), random(c, d))";

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Collections.singletonList(input)));
    }

    @Test
    public void splitThingGroup2() {
        String group1 = "all(random(a, b), random(c, d))";
        String group2 = "random(all(e, f), all(g, h))";
        String input = group1 + ", " + group2;

        List<String> result = ParserUtil.split(input);

        assertThat(result, equalTo(Arrays.asList(group1, group2)));
    }

}
