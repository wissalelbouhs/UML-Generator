package org.mql.java.info;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InterfaceDetails {
    private String shortName;
    private String fullName;
    private String accessModifiers;
    private String parentInterface;
    private List<FieldDetails> interfaceFields;
    private List<MethodDetails> interfaceMethods;
    
    

    public InterfaceDetails() {
    	
    }

    public InterfaceDetails(Class<?> cls) {
        shortName = cls.getSimpleName();
        fullName = cls.getName();
        assignModifiers(cls);
        assignParentInterface(cls);
        initializeFields(cls);
        initializeMethods(cls);
    }

    public InterfaceDetails(String path) throws ClassNotFoundException {
        this(Class.forName(path));
    }


    private void assignParentInterface(Class<?> cls) {
        if (cls.getSuperclass() == null) return;
        parentInterface = cls.getSuperclass().getName();

        if ("java.lang.Object".equals(parentInterface)) {
            parentInterface = null;
        }
    }

    private void assignModifiers(Class<?> cls) {
        accessModifiers = Modifier.toString(cls.getModifiers());
    }

    private void initializeFields(Class<?> cls) {
        interfaceFields = new ArrayList<>();

        for (Field field : cls.getDeclaredFields()) {
            interfaceFields.add(new FieldDetails(field));
        }
    }

    private void initializeMethods(Class<?> cls) {
        interfaceMethods = new ArrayList<>();

        for (Method method : cls.getDeclaredMethods()) {
            interfaceMethods.add(new MethodDetails(method));
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAccessModifiers() {
        return accessModifiers;
    }

    public String getParentInterface() {
        return parentInterface;
    }

    public List<FieldDetails> getInterfaceFields() {
        return interfaceFields;
    }

    public List<MethodDetails> getInterfaceMethods() {
        return interfaceMethods;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

