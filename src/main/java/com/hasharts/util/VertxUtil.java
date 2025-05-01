package com.hasharts.util;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Context;
import io.vertx.mutiny.core.Vertx;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VertxUtil {

    public <T> Uni<T> runOnContext(Uni<T> uni) {
        Context context = Vertx.currentContext();
        if (context != null) {  // used w/o context (for example in tests or on startup)
            return uni.emitOn(context::runOnContext);
        } else {
            return uni;
        }
    }
}
