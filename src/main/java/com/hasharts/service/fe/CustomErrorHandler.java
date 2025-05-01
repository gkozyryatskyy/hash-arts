package com.hasharts.service.fe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class CustomErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent errorEvent) {
        Throwable t = errorEvent.getThrowable();
        log.error(errorEvent.getThrowable());
        if (UI.getCurrent() != null) {
            UI.getCurrent()
                    .access(() -> Notification.show("Error. Message: " + t.getMessage(), 3000, Position.TOP_CENTER)
                            .addThemeVariants(
                                    NotificationVariant.LUMO_ERROR));
        }
    }
}
