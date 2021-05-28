package ru.monad.analysis.logic.imperative;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;

@Singleton
public class GetDocumentByIdOperation {

    private static final Logger log = LoggerFactory.getLogger(GetDocumentByIdOperation.class);

    private final DocumentService documentService;

    @Inject
    public GetDocumentByIdOperation(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Document process(Long documentId) {
        log.info("GetDocumentByIdOperation.process.int");
        try {
            var result = internalProcess(documentId);
            log.info("GetDocumentByIdOperation.process.out");
            return result;
        } catch (Exception e) {
            log.error("GetDocumentByIdOperation.process.thrown", e);
            throw new RuntimeException(e);
        }
    }

    private Document internalProcess(Long id) {
        var content = documentService.findContentByDocumentId(id);
        if (isNull(content)) {
            content = new ByteArrayInputStream(new byte[0]);
        }
        var properties = documentService.findPropertiesByDocumentId(id);
        if (isNull(properties)) {
            properties = emptyMap();
        }
        return new Document(id, content, properties);
    }
}
