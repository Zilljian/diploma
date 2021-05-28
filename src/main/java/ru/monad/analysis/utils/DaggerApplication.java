package ru.monad.analysis.utils;


import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DaggerApplication {

    @SafeVarargs
    static <T extends DaggerApplication> T run(Supplier<T> supplier, Consumer<T> starter, Consumer<T>... stoppers) {
        T ctx = supplier.get();
        var log = LoggerFactory.getLogger(ctx.getClass());
        try {
            starter.accept(ctx);
        } catch (Exception e) {
            log.info("Application start failed", e);
            shutdown(ctx, stoppers);
            System.exit(-1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Application shutdown begin");
            shutdown(ctx, stoppers);
        }));
        return ctx;
    }

    @SafeVarargs
    static <T extends DaggerApplication> void shutdown(T ctx, Consumer<T>... stoppers) {
        var log = LoggerFactory.getLogger(ctx.getClass());
        var e = (Exception) null;

        for (var stopper : stoppers) {
            try {
                stopper.accept(ctx);
            } catch (Exception var10) {
                e = suppressOrReturn(e, var10);
            }
        }

        if (e != null) {
            log.info("Application shutdown end with errors", e);
        } else {
            log.info("Application shutdown end");
        }
    }

    private static Exception suppressOrReturn(Exception exception, Exception newException) {
        if (exception != null) {
            exception.addSuppressed(newException);
            return exception;
        } else {
            return newException;
        }
    }
}
