package ru.monad.analysis;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamTest {

    private static final String INPUT_STRING = "2000, 2004, 2012, 2018";
    private static final List<Integer> INPUT_INTEGERS = List.of(2000, 2004, 2012, 2018);
    private static final List<String> INPUT_LIST = List.of("2000", "2004", "2012", "2018");

    /**
     * (return a) bind f <=> f a
     */
    @Test
    void testLeftIdentity() {
        Function<String, Stream<String>> split = s -> Stream.of(s.split(","));

        var rawFunctionUsage = split
                .apply(INPUT_STRING)
                .collect(toList());
        var streamMonadFunctionUsage = Stream.of(INPUT_STRING)
                .flatMap(split)
                .collect(toList());

        assertEquals(streamMonadFunctionUsage, rawFunctionUsage);
    }

    /**
     * m bind unit <=> m
     */
    @Test
    void testRightIdentity() {
        var initialList = INPUT_LIST;
        var steamWithIdentityModification = initialList.stream()
                .flatMap(Stream::of)
                .collect(toList());

        assertEquals(initialList, steamWithIdentityModification);
    }

    /**
     * (m bind f) bind g <=> m bind (\x -> f x bind g)
     */
    @Test
    void testAssociativity() {
        Function<Integer, Stream<String>> f = i -> Arrays
                .stream(Integer.toHexString(i).split(""));
        Function<String, Stream<Integer>> g = s -> Stream.of(s)
                .map(str -> Integer.parseInt(str, 16));

        var leftResult = INPUT_INTEGERS.stream()
                .flatMap(f)
                .flatMap(g)
                .collect(toList());
        var rightResult = INPUT_INTEGERS.stream()
                .flatMap(i -> f
                        .apply(i)
                        .flatMap(g))
                .collect(toList());

        assertEquals(leftResult, rightResult);
    }
}
