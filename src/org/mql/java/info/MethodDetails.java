package org.mql.java.info;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodDetails {
    private String methodName;
    private String returnType;
    private String parameters;
    private String accessModifier;

    public MethodDetails(Method method) {
        this.methodName = method.getName();
        this.returnType = method.getReturnType().getSimpleName();
        this.accessModifier = determineAccessModifier(method.getModifiers());

        StringBuilder paramBuilder = new StringBuilder("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            paramBuilder.append(parameterTypes[i].getSimpleName());
            if (i < parameterTypes.length - 1) {
                paramBuilder.append(", ");
            }
        }
        paramBuilder.append(")");
        this.parameters = paramBuilder.toString();
    }

    private String determineAccessModifier(int modifiers) {
        if (Modifier.isPublic(modifiers)) return "+";
        if (Modifier.isPrivate(modifiers)) return "-";
        if (Modifier.isProtected(modifiers)) return "#";
        return "~";
    }

    public String getRepresentation() {
        return accessModifier + " " + methodName + parameters + " : " + returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getParameters() {
        return parameters;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }
}
