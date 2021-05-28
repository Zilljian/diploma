package ru.monad.analysis.logic.imperative;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.monad.analysis.model.Document;
import ru.monad.analysis.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;

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
        var ids =  documentService.find();
        var result = new ArrayList<Document>(ids.size());
        for (var id : ids) {
            var content = documentService.findContentByDocumentId(id);
            if (isNull(content)) {
                content = new ByteArrayInputStream(new byte[0]);
            }
            var properties = documentService.findPropertiesByDocumentId(id);
            if (isNull(properties)) {
                properties = emptyMap();
            }
            result.add(new Document(id, content, properties));
        }
        return result;
    }
}
