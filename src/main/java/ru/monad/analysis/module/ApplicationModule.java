package ru.monad.analysis.module;

import dagger.Module;
import dagger.Provides;
import ru.monad.analysis.service.DocumentService;

@Module
public interface ApplicationModule {

    @Provides
    static DocumentService documentService() {
        return new DocumentService();
    }
}
