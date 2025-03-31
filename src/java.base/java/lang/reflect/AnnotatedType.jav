package java.lang.reflect;

import java.lang.annotation.Annotation;

public interface AnnotatedType extends AnnotatedElement {
    default AnnotatedType getAnnotatedOwnerType() {
        return null;
    }

    public Type getType();

    @Override
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    @Override
    Annotation[] getAnnotations();

    @Override
    Annotation[] getDeclaredAnnotations();
}
