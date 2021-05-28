package jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static jmh.LogicBenchmarks.CPU_LOAD;

public class RawComparisonBenchmarks {

    public static void main(String[] args) throws RunnerException {
        var options = new OptionsBuilder()
                .shouldDoGC(true)
                .resultFormat(ResultFormatType.JSON)
                .result("results/" + System.currentTimeMillis() + ".json")
                .mode(Mode.Throughput)
                .warmupIterations(1)
                .measurementIterations(5)
                .forks(2)
                .build();
        new Runner(options).run();
    }

    private Integer expensiveOperation() {
        Blackhole.consumeCPU(CPU_LOAD);
        return CPU_LOAD;
    }

    @Benchmark
    public void sillyExecution() {
        var result = expensiveOperation() * expensiveOperation();
    }

    @Benchmark()
    public void rawStream() {
        IntStream.range(0, 2)
                .mapToObj(x -> x == 0 ? expensiveOperation() : expensiveOperation())
                .reduce(1, (a, b) -> a * b);
    }

    @Benchmark
    public void parallelStream() {
        IntStream.range(0, 2)
                .parallel()
                .mapToObj(x -> x == 0 ? expensiveOperation() : expensiveOperation())
                .reduce(1, (a, b) -> a * b);
    }

    @Benchmark
    public void rawFuture() throws InterruptedException, ExecutionException {
        var threadPool = ForkJoinPool.commonPool();
        var task1 = threadPool.submit(this::expensiveOperation);
        var task2 = threadPool.submit(this::expensiveOperation);
        var result = task1.get() * task2.get();
    }

    @Benchmark
    public void wrappedFuture() throws InterruptedException, ExecutionException {
        var threadPool = ForkJoinPool.commonPool();
        var task1 = threadPool.submit(this::expensiveOperation);
        var task2 = threadPool.submit(this::expensiveOperation);
        threadPool.submit(() -> task1.get() * task2.get())
                .get();
    }

    @Benchmark
    public void rawCompletableFuture() throws InterruptedException, ExecutionException {
        var cfa = CompletableFuture.supplyAsync(this::expensiveOperation);
        var cfb = CompletableFuture.supplyAsync(this::expensiveOperation);
        var result = cfa.get() * cfb.get();
    }

    @Benchmark
    public void wrappedCompletableFuture() throws InterruptedException, ExecutionException {
        CompletableFuture
                .supplyAsync(this::expensiveOperation)
                .thenCombine(
                        CompletableFuture
                                .supplyAsync(this::expensiveOperation), (a, b) -> a * b)
                .get();
    }
}
