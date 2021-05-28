package jmh;

import org.openjdk.jmh.annotations.Benchmark;
import ru.monad.analysis.logic.functional.GetDocumentByIdOperation;
import ru.monad.analysis.logic.imperative.FindDocumentOperation;
import ru.monad.analysis.service.DocumentService;

public class LogicBenchmarks {

    public static final int CPU_LOAD = 0;

    private static final DocumentService documentService = new DocumentService();

    private static final ru.monad.analysis.logic.functional.FindDocumentOperation functionalFindDocumentOperation = new ru.monad.analysis.logic.functional.FindDocumentOperation(documentService);
    private static final FindDocumentOperation imperativeFindDocumentOperation = new FindDocumentOperation(documentService);
    private static final ru.monad.analysis.logic.reactor.FindDocumentOperation reactiveFindDocumentOperation = new ru.monad.analysis.logic.reactor.FindDocumentOperation(documentService);

    private static final GetDocumentByIdOperation functionalGetDocumentByIdOperation = new GetDocumentByIdOperation(documentService);
    private static final ru.monad.analysis.logic.imperative.GetDocumentByIdOperation imperativeGetDocumentByIdOperation = new ru.monad.analysis.logic.imperative.GetDocumentByIdOperation(documentService);
    private static final ru.monad.analysis.logic.reactor.GetDocumentByIdOperation reactiveGetDocumentByIdOperation = new ru.monad.analysis.logic.reactor.GetDocumentByIdOperation(documentService);

    @Benchmark
    public void functionalSearch() {
        functionalFindDocumentOperation.process();
    }

    @Benchmark
    public void reactiveSearch() {
        reactiveFindDocumentOperation.process()
                .subscribe();
    }

    @Benchmark
    public void imperativeSearch() {
        imperativeFindDocumentOperation.process();
    }
}
