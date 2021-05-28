package ru.monad.analysis;


import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OptionalTest {

    private final Map<String, Integer> INPUT_MAP = Map.of(
            "One", 1,
            "Two", 2,
            "Three", 3
    );
    private static final String INPUT_STRING = "Some string";

    /**
     * (return a) bind f <=> f a
     */
    @Test
    void sillyTestLeftIdentity() {
        Function<String, Optional<Integer>> getExact = k -> Optional.ofNullable(INPUT_MAP.get(k));

        var rawFunctionUsage = getExact
                .apply("Four");
        var optionalMonadFunctionUsage = Optional.of("Four")
                .flatMap(getExact);

        assertEquals(optionalMonadFunctionUsage, rawFunctionUsage);
    }

    @Test
    void breakingTestLeftIdentity() {
        Function<String, Optional<Integer>> getExact = k -> {
            if (isNull(k)) {
                return Optional.ofNullable(INPUT_MAP.get("One"));
            }
            return Optional.ofNullable(INPUT_MAP.get(k));
        };

        var rawFunctionUsage = getExact
                .apply("Four");
        var optionalMonadFunctionUsage = Optional.of("Four")
                .flatMap(getExact);
        var rawFunctionUsageWithNull = getExact
                .apply(null);
        var optionalMonadFunctionUsageWithNull = Optional.<String>ofNullable(null)
                .flatMap(getExact);

        assertEquals(optionalMonadFunctionUsage, rawFunctionUsage);

        assertNotEquals(rawFunctionUsageWithNull, optionalMonadFunctionUsageWithNull);
    }

    @Test
    void correctedTestLeftIdentity() {
        Function<String, Optional<Integer>> getExact = k -> {
            if (isNull(k)) {
                return Optional.empty();
            } else if (k.isBlank()) {
                k = "One";
            }
            return Optional.ofNullable(INPUT_MAP.get(k));
        };

        var rawFunctionUsage = getExact
                .apply("Four");
        var optionalMonadFunctionUsage = Optional.of("Four")
                .flatMap(getExact);
        var rawFunctionUsageWithNull = getExact
                .apply("");
        var optionalMonadFunctionUsageWithNull = Optional.ofNullable("")
                .flatMap(getExact);

        assertEquals(optionalMonadFunctionUsage, rawFunctionUsage);

        assertNotEquals(rawFunctionUsageWithNull, optionalMonadFunctionUsageWithNull);
    }

    /**
     * m bind unit <=> m
     */
    @Test
    void testRightIdentity() {
        var initialList = Optional.of(INPUT_STRING);
        var steamWithIdentityModification = Optional.of(INPUT_STRING)
                .flatMap(Optional::of);

        assertEquals(initialList, steamWithIdentityModification);
    }

    /**
     * (m bind f) bind g <=> m bind (\x -> f x bind g)
     */
    @Test
    void testAssociativity() {
        Function<Integer, Optional<String>> f = i ->
                Optional.of(Integer.toHexString(i));
        Function<String, Optional<Integer>> g = s ->
                Optional.of(s).map(str -> Integer.parseInt(str, 16));

        var leftResult = Optional.of(2000)
                .flatMap(f)
                .flatMap(g);
        var rightResult = Optional.of(2000)
                .flatMap(i -> f
                        .apply(i)
                        .flatMap(g));

        assertEquals(leftResult, rightResult);
    }
}
