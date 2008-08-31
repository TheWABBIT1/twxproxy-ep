package org.twdata.twxbbs.event.impl;

import org.twdata.twxbbs.event.EventListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Listener method selector that looks for a specific marker annotation
 */
public class AnnotationListenerMethodSelector implements ListenerMethodSelector
{
    private final Class<? extends Annotation> markerAnnotation;

    public AnnotationListenerMethodSelector()
    {
        this(EventListener.class);
    }

    public AnnotationListenerMethodSelector(Class<? extends Annotation> markerAnnotation)
    {
        this.markerAnnotation = markerAnnotation;
    }
    public boolean isListenerMethod(Method method)
    {
        return (method.getAnnotation(markerAnnotation) != null);
    }
}
