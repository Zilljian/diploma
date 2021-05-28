package ru.monad.analysis.logic.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;
import ru.monad.analysis.utils.Util;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

@Singleton
public class FindDocumentOperation {

    private static final Logger log = LoggerFactory.getLogger(FindDocumentOperation.class);

    private final DocumentService documentService;

    @Inject
    public FindDocumentOperation(DocumentService documentService) {
        this.documentService = documentService;
    }

    public List<Document> process() {
        log.info("FindDocumentOperation.process.int");
        try {
            var result = internalProcess();
            log.info("FindDocumentOperation.process.out");
            return result;
        } catch (Exception e) {
            log.error("FindDocumentOperation.process.thrown", e);
            throw new RuntimeException(e);
        }
    }

    private List<Document> internalProcess() {
        return documentService
                .find()
                .stream()
                .map(id -> Util.zip(
                        () -> documentService
                                .findContentByDocumentIdMonad(id)
                                .orElse(new ByteArrayInputStream(new byte[0])),
                        () -> documentService
                                .findPropertiesByDocumentIdMaybe(id)
                                .orElse(emptyMap()))
                        .apply((c, d) -> new Document(id, c, d))
                )
                .map(r -> (Document) r.join())
                .collect(toList());
    }
}
