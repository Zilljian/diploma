## This repo is a part of the final work for Bachelor Degree Graduation in Software Engineering. 
A diploma explores the monad interfaces in modern programming languages using the example of Java. The formal theme "Monad Data Structure Utilization Across Modern Programming Languages". \
In this work 3 main functional interfaces are considered:
- `Optional`
- `Stream`
- `CompletableFuture`
### Build and Run
In order to run tests: \
`$ mvn clean test` \
\
In order to run benchmarks: \
`$ mvn clean compile`  and then run `jmh.RawComparisonBenchmarks.main` \
All benchmarks results are stored in the results directory.