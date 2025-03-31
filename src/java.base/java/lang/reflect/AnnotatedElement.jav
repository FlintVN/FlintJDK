package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface AnnotatedElement {
    default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    Annotation[] getAnnotations();

    default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        // T[] result = getDeclaredAnnotationsByType(annotationClass);
        // if (result.length == 0 && this instanceof Class && AnnotationType.getInstance(annotationClass).isInherited()) {
        //     Class<?> superClass = ((Class<?>) this).getSuperclass();
        //     if (superClass != null)
        //         result = superClass.getAnnotationsByType(annotationClass);
        // }
        // return result;

        // TODO
        throw new UnsupportedOperationException();
    }

    default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        for(Annotation annotation : getDeclaredAnnotations()) {
            if(annotationClass.equals(annotation.annotationType()))
                return annotationClass.cast(annotation);
        }
        return null;
    }

    default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        // Objects.requireNonNull(annotationClass);
        // return AnnotationSupport.getDirectlyAndIndirectlyPresent(
        //     Arrays.stream(getDeclaredAnnotations()).collect(Collectors.toMap(Annotation::annotationType,
        //     Function.identity(),
        //     ((first,second) -> first),
        //     LinkedHashMap::new)),
        //     annotationClass
        // );

        // TODO
        throw new UnsupportedOperationException();
    }

    Annotation[] getDeclaredAnnotations();
}
