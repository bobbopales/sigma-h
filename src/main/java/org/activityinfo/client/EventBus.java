package org.activityinfo.client;

import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Observable;
import com.google.inject.Singleton;
import com.google.gwt.core.client.GWT;


public interface EventBus extends Observable {

    static class NamedEventType extends EventType {

        private final String name;

        public NamedEventType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };


    public boolean fireEvent(EventType type);
    
    public boolean fireEvent(BaseEvent event);
}
