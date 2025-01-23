package org.mql.java.info;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDetails {
    private String annotationName;
    private RetentionPolicy retentionPolicy;
    private boolean inherited;
    private Map<String, String> attributes;
    
    public AnnotationDetails() {
		// TODO Auto-generated constructor stub
	}

    public AnnotationDetails(String annotationClassPath) {
        attributes = new HashMap<>();
        try {
            Class<?> annotationClass = Class.forName(annotationClassPath);

            annotationName = extractAnnotationName(annotationClass);
            retentionPolicy = extractRetentionPolicy(annotationClass);
            inherited = isAnnotationInherited(annotationClass);
            attributes = extractAttributes(annotationClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractAnnotationName(Class<?> annotationClass) {
        return annotationClass.getSimpleName();
    }

    private RetentionPolicy extractRetentionPolicy(Class<?> annotationClass) {
        Retention retention = annotationClass.getAnnotation(Retention.class);
        return (retention != null) ? retention.value() : RetentionPolicy.CLASS;
    }

    private boolean isAnnotationInherited(Class<?> annotationClass) {
        return annotationClass.isAnnotationPresent(Inherited.class);
    }

    private Map<String, String> extractAttributes(Class<?> annotationClass) {
        Map<String, String> attributes = new HashMap<>();
        for (Method method : annotationClass.getDeclaredMethods()) {
            attributes.put(method.getName(), method.getReturnType().getSimpleName());
        }
        return attributes;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public RetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public boolean isInherited() {
        return inherited;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "AnnotationDetails{" +
                "name='" + annotationName + '\'' +
                ", retentionPolicy=" + retentionPolicy +
                ", inherited=" + inherited +
                ", attributes=" + attributes +
                '}';
    }

}
