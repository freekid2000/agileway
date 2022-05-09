package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappings {
    public static boolean hasAnyRequestMappingAnnotation(Method method){
        return findFirstRequestMappingAnnotation(method)!= null;
    }

    public static Annotation findFirstRequestMappingAnnotation(AnnotatedElement method) {
        return Pipeline.of(Reflects.getAnnotations(method)).findFirst(new Predicate<Annotation>() {
            @Override
            public boolean test(Annotation annotation) {
                return isRequestMappingAnnotation(annotation);
            }
        });
    }

    private static final List<Class<? extends Annotation>> requestMappingAnnotations = Collects.newArrayList(
            HttpMethod.class,
            GET.class,
            DELETE.class,
            PATCH.class,
            POST.class,
            PUT.class,
            HEAD.class,
            OPTIONS.class
    );

    public static boolean isRequestMappingAnnotation(@NonNull final Annotation annotation) {
        Preconditions.checkNotNull(annotation);
        return Collects.anyMatch(requestMappingAnnotations, new Predicate<Class<? extends Annotation>>() {
            @Override
            public boolean test(Class<? extends Annotation> clazz) {
                return Reflects.isSubClassOrEquals(clazz, annotation.getClass());
            }
        });
    }

    public static RequestMappingAccessor<?> createAccessor(@NonNull Method method,@NonNull Annotation annotation,Produces produces, Consumes consumes, Path pathAnno  ) {
        Preconditions.checkNotNull(annotation);
        RequestMappingAccessor accessor = null;
        /*
        if (annotation instanceof HttpMethod) {
            accessor = new HttpMethodAnnotationAccessor();
            accessor.setMapping((RequestMapping) annotation);
        } else if (annotation instanceof DeleteMapping) {
            accessor = new DeleteMappingAnnotationAccessor();
            accessor.setMapping((DeleteMapping) annotation);
        } else if (annotation instanceof GetMapping) {
            accessor = new GetMappingAnnotationAccessor();
            accessor.setMapping((GetMapping) annotation);
        } else if (annotation instanceof PatchMapping) {
            accessor = new PatchMappingAnnotationAccessor();
            accessor.setMapping((PatchMapping) annotation);
        } else if (annotation instanceof PostMapping) {
            accessor = new PostMappingAnnotationAccessor();
            accessor.setMapping((PostMapping) annotation);
        } else if (annotation instanceof PutMapping) {
            accessor = new PutMappingAnnotationAccessor();
            accessor.setMapping((PutMapping) annotation);
        }
         */
        if (accessor == null) {
            throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("{} is not a Spring RequestMapping annotation", annotation.getClass().getSimpleName()));
        }
        return accessor;
    }
}
