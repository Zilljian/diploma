package ru.monad.analysis.module;

import dagger.Component;
import ru.monad.analysis.service.DocumentService;
import ru.monad.analysis.utils.DaggerApplication;

import javax.inject.Singleton;

@Component(modules = ApplicationModule.class)
@Singleton
public interface ApplicationComponent extends DaggerApplication {

    DocumentService documentService();
}
