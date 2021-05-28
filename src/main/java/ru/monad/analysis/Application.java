package ru.monad.analysis;

import ru.monad.analysis.module.DaggerApplicationComponent;
import ru.monad.analysis.utils.DaggerApplication;

public class Application {

    public static void main(String[] args) {
        DaggerApplication.run(
                DaggerApplicationComponent.builder()
                        ::build,
                ctx -> {}
        );
    }
}
