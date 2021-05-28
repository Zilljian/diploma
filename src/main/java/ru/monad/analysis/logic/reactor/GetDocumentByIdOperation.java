package ru.monad.analysis.logic.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetDocumentByIdOperation {

    private static final Logger log = LoggerFactory.getLogger(FindDocumentOperation.class);

    private final DocumentService documentService;

    @Inject
    public GetDocumentByIdOperation(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Mono<Document> process(Long documentId) {
        var content = documentService.findContentByDocumentIdFlux(documentId);
        var properties = documentService.findPropertiesByDocumentIdFlux(documentId)
                .collectMap(Tuple2::getT1, Tuple2::getT2);
        return Mono.zip(content, properties)
                .map(t2 -> new Document(documentId, t2.getT1(), t2.getT2()))
                .doOnSubscribe(s -> log.info("GetDocumentByIdOperation.process.in"))
                .doOnSuccess(d -> log.info("GetDocumentByIdOperation.process.out"))
                .onErrorMap(RuntimeException::new)
                .doOnError(e -> log.error("GetDocumentByIdOperation.process.thrown", e));
    }
}
