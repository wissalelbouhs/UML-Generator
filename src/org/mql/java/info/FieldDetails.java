package org.mql.java.info;

import java.lang.reflect.*;
import java.util.*;

public class FieldDetails {
    private String fieldName;
    private String fieldType;
    private String simpleTypeName;
    private String accessModifier;
    private boolean estTypeExterne;
    private boolean isCollection;
    
    public FieldDetails() {
		// TODO Auto-generated constructor stub
	}

    public FieldDetails(Field field) {
        this.fieldName = field.getName();
        this.fieldType = field.getType().getName();
        this.simpleTypeName = field.getType().getSimpleName();
        this.accessModifier = determineAccessModifier(field.getModifiers());
        this.estTypeExterne = checkIfExternalType(field);
        this.isCollection = checkIfCollection(field);
    }

    private String determineAccessModifier(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "+";
        } else if (Modifier.isPrivate(modifiers)) {
            return "-";
        } else if (Modifier.isProtected(modifiers)) {
            return "#";
        } else {
            return "~";
        }
    }


    private boolean checkIfCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private boolean checkIfExternalType(Field field) {
        Class<?> fieldClass = field.getType();
        Package fieldPackage = fieldClass.getPackage();

        if (fieldPackage == null) {
            return true;
        }

        String packageName = fieldPackage.getName();
        return !packageName.startsWith("java.");
    }

    public String toStringRepresentation() {
        return accessModifier + " " + fieldName + " : " + simpleTypeName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getSimpleTypeName() {
        return this.simpleTypeName;
    }

    public void setSimpleTypeName(String simpleTypeName) {
        this.simpleTypeName = simpleTypeName;
    }

    public String getAccessModifier() {
        return this.accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public boolean isEstTypeExterne() {
        return this.estTypeExterne;
    }

    public void setEstTypeExterne(boolean estTypeExterne) {
        this.estTypeExterne = estTypeExterne;
    }

    public boolean isCollection() {
        return this.isCollection;
    }

    public void setCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }
}

