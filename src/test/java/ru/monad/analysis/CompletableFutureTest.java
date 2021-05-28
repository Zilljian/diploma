package ru.monad.analysis;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletableFutureTest {

    private static final String CONSTANT_RESULT = "Completed";

    /**
     * (return a) bind f <=> f a
     */
    @Test
    void testLeftIdentity() throws ExecutionException, InterruptedException {
        Function<String, CompletableFuture<String>> appendOnComplete = s -> CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s.concat(" with map result");
                });

        var leftResult = CompletableFuture
                .completedFuture(CONSTANT_RESULT)
                .thenComposeAsync(appendOnComplete)
                .get();

        var rightResult = appendOnComplete
                .apply(CONSTANT_RESULT)
                .get();
        assertEquals(leftResult, rightResult);
    }

    /**
     * m bind unit <=> m
     */
    @Test
    void testRightIdentity() throws ExecutionException, InterruptedException {
        Function<String, CompletableFuture<String>> identityCompletableFuture = s -> CompletableFuture.supplyAsync(() -> s);
        var rightResult = CompletableFuture
                .completedFuture(CONSTANT_RESULT)
                .thenCompose(identityCompletableFuture)
                .get();

        assertEquals(CONSTANT_RESULT, rightResult);
    }

    /**
     * (m bind f) bind g <=> m bind (\x -> f x bind g)
     */
    @Test
    void testAssociativity() throws ExecutionException, InterruptedException {
        Function<Integer, CompletableFuture<String>> f = i -> CompletableFuture
                .supplyAsync(() -> String.join("", Integer
                        .toHexString(i)
                        .split("")));
        Function<String, CompletableFuture<Integer>> g = s -> CompletableFuture
                .supplyAsync(() -> Stream.of(s)
                        .map(str -> Integer.parseInt(str, 16))
                        .mapToInt(i -> i)
                        .sum());

        var completableFuture = CompletableFuture
                .completedFuture(2000);

        var leftResult = completableFuture
                .thenCompose(f)
                .thenCompose(g)
                .get();
        var rightResult = completableFuture
                .thenCompose(r -> f
                        .apply(r)
                        .thenCompose(g))
                .get();

        assertEquals(leftResult, rightResult);
    }
}
