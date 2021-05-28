package ru.monad.analysis.logic.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FindDocumentOperation {

    private static final Logger log = LoggerFactory.getLogger(FindDocumentOperation.class);

    private final DocumentService documentService;

    @Inject
    public FindDocumentOperation(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Flux<Document> process() {
        return documentService
                .findFlux()
                .flatMap(id -> {
                    var content = documentService.findContentByDocumentIdFlux(id);
                    var properties = documentService.findPropertiesByDocumentIdFlux(id)
                            .collectMap(Tuple2::getT1, Tuple2::getT2);
                    return Mono.zip(content, properties, Mono.just(id));
                })
                .map(t3 -> new Document(t3.getT3(), t3.getT1(), t3.getT2()))
                .doOnSubscribe(s -> log.info("FindDocumentOperation.process.in"))
                .doOnComplete(() -> log.info("FindDocumentOperation.process.out"))
                .onErrorMap(RuntimeException::new)
                .doOnError(e -> log.error("FindDocumentOperation.process.thrown", e));
    }
}
