package ru.monad.analysis.logic.functional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;

import static java.util.Collections.emptyMap;
import static ru.monad.analysis.utils.Util.zip;

@Singleton
public class GetDocumentByIdOperation {

    private static final Logger log = LoggerFactory.getLogger(GetDocumentByIdOperation.class);

    private final DocumentService documentService;

    @Inject
    public GetDocumentByIdOperation(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Document process(Long documentId) {
        log.info("GetDocumentByIdFunctionalOperation.process.int");
        try {
            var result = internalProcess(documentId);
            log.info("GetDocumentByIdFunctionalOperation.process.out");
            return result;
        } catch (Exception e) {
            log.error("GetDocumentByIdFunctionalOperation.process.thrown", e);
            throw new RuntimeException(e);
        }
    }

    private Document internalProcess(Long id) {
        return (Document) zip(
                () -> documentService
                        .findContentByDocumentIdMonad(id)
                        .orElse(new ByteArrayInputStream(new byte[0])),
                () -> documentService
                        .findPropertiesByDocumentIdMaybe(id)
                        .orElse(emptyMap()))
                .apply((c, d) -> new Document(id, c, d))
                .join();
    }
}
