package ru.monad.analysis.utils;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Util {

    private static final Executor executor = Executors.newWorkStealingPool(8);

    public static Map.Entry<String, Object> randomProperty() {
        return Map.entry(generateKey(), generateValue());
    }

    public static String generateKey() {
        return UUID.randomUUID().toString();
    }

    public static Mono<String> generateKeyMono() {
        return Mono.just(UUID.randomUUID().toString());
    }

    public static Object generateValue() {
        var rand = ThreadLocalRandom.current().nextInt(5) + 1;
        return rand / 2 == 0 ?
                ThreadLocalRandom.current().nextInt() :
                UUID.randomUUID().toString();
    }

    public static Mono<?> generateValueMono() {
        return Mono.fromSupplier(Util::generateValue);
    }

    public static <T, U, V> Function<BiFunction<T, U, V>, CompletableFuture<V>> zip(Supplier<T> f, Supplier<U> g) {
        return y -> withAsync(f).thenCombineAsync(withAsync(g), y);
    }

    public static <T> CompletableFuture<T> withAsync(Supplier<T> f) {
        return new CompletableFuture<T>().completeAsync(f, executor);
    }
}
