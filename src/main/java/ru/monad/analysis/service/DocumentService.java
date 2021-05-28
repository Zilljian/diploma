package ru.monad.analysis.service;


import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.monad.analysis.utils.Util;

import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;
import static jmh.LogicBenchmarks.CPU_LOAD;

@Singleton
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    public Optional<Map<String, ?>> findPropertiesByDocumentIdMaybe(Long documentId) {
        return Optional.ofNullable(findPropertiesByDocumentId(documentId));
    }

    public Map<String, ?> findPropertiesByDocumentId(Long documentId) {
        log.info("DocumentService.findPropertiesByDocumentId.in documentId = {}", documentId);
        var size = ThreadLocalRandom.current().nextInt(20) + 1;
        var map = new HashMap<String, Object>(size);
        for (var i = 0; i < size; i++) {
            var entry = Util.randomProperty();
            map.put(entry.getKey(), entry.getValue());
        }
        Blackhole.consumeCPU(CPU_LOAD);
        log.info("DocumentService.findPropertiesByDocumentId.out resulting map = {}", map);
        return map;
    }

    public Flux<? extends Tuple2<String, ?>> findPropertiesByDocumentIdFlux(Long documentId) {
        var propertyFlux = Flux.<Mono<String>>create(sink -> {
            var size = ThreadLocalRandom.current().nextInt(20) + 1;
            while (size-- >= 0) {
                sink.next(Util.generateKeyMono());
            }
            sink.complete();
        });
        return Mono.fromRunnable(() -> Blackhole.consumeCPU(CPU_LOAD))
                .thenMany(propertyFlux)
                .flatMap(k -> k.zipWith(Util.generateValueMono()))
                .doOnSubscribe(s -> log.info("DocumentService.findPropertiesByDocumentId.in documentId = {}", documentId))
                .doOnComplete(() -> log.info("DocumentService.findPropertiesByDocumentId.out"));
    }

    public Optional<InputStream> findContentByDocumentIdMonad(Long documentId) {
        log.info("DocumentService.findContentByDocumentId.in documentId = {}", documentId);
        var length = ThreadLocalRandom.current().nextInt(10240) + 1;
        var array = new byte[length];
        ThreadLocalRandom.current().nextBytes(array);
        Blackhole.consumeCPU(CPU_LOAD);
        log.info("DocumentService.findContentByDocumentId.out");
        return Optional.ofNullable(new ByteArrayInputStream(array));
    }

    public InputStream findContentByDocumentId(Long documentId) {
        log.info("DocumentService.findContentByDocumentId.in documentId = {}", documentId);
        var length = ThreadLocalRandom.current().nextInt(10240) + 1;
        var array = new byte[length];
        ThreadLocalRandom.current().nextBytes(array);
        Blackhole.consumeCPU(CPU_LOAD);
        log.info("DocumentService.findContentByDocumentId.out");
        return new ByteArrayInputStream(array);
    }

    public Mono<? extends InputStream> findContentByDocumentIdFlux(Long documentId) {
        var length = ThreadLocalRandom.current().nextInt(10240) + 1;
        var array = new byte[length];
        ThreadLocalRandom.current().nextBytes(array);
        var byteBuf = Unpooled.wrappedBuffer(array);
        var documentContentMono = Mono.just(new ByteBufInputStream(byteBuf));
        return Mono.fromRunnable(() -> Blackhole.consumeCPU(CPU_LOAD))
                .then(documentContentMono)
                .doOnSubscribe(s -> log.info("DocumentService.findContentByDocumentId.in documentId = {}", documentId))
                .doOnSuccess(r -> log.info("DocumentService.findContentByDocumentId.out"));
    }

    public List<Long> find() {
        var size = ThreadLocalRandom.current().nextLong(20) + 1;
        return ThreadLocalRandom.current()
                .longs(size, 1L, 1000)
                .boxed()
                .distinct()
                .collect(toList());
    }

    public Flux<Long> findFlux() {
        return Flux.<Long>create(
                sink -> {
                    var size = ThreadLocalRandom.current().nextLong(20) + 1;
                    ThreadLocalRandom.current()
                            .longs(size, 1L, 1000)
                            .forEach(sink::next);
                    sink.complete();
                })
                .distinct();
    }
}
