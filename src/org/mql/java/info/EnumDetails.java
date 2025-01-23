package org.mql.java.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EnumDetails {
    private String qualifiedName;
    private String displayName;
    private List<String> enumConstants;

    public EnumDetails(String enumPath) {
        initializeEnumDetails(enumPath);
    }

    private void initializeEnumDetails(String enumPath) {
        try {
            processEnumClass(loadEnumClass(enumPath));
        } catch (ClassNotFoundException ex) {
            handleClassLoadError(enumPath);
        }
    }

    private Class<?> loadEnumClass(String enumPath) throws ClassNotFoundException {
        return Class.forName(enumPath);
    }

    private void processEnumClass(Class<?> enumClass) {
        this.qualifiedName = enumClass.getName();
        this.displayName = enumClass.getSimpleName();
        this.enumConstants = extractEnumConstants(enumClass);
    }

    private List<String> extractEnumConstants(Class<?> enumClass) {
        List<String> constants = new ArrayList<>();
        Optional.ofNullable(enumClass.getEnumConstants())
               .ifPresent(enums -> {
                   for (Object enumConstant : enums) {
                       constants.add(enumConstant.toString());
                   }
               });
        return constants;
    }

    private void handleClassLoadError(String enumPath) {
        this.qualifiedName = enumPath;
        this.displayName = extractSimpleName(enumPath);
        this.enumConstants = new ArrayList<>();
    }

    private String extractSimpleName(String qualifiedPath) {
        int lastDotIndex = qualifiedPath.lastIndexOf('.');
        return lastDotIndex >= 0 ? qualifiedPath.substring(lastDotIndex + 1) : qualifiedPath;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getEnumConstants() {
        return Collections.unmodifiableList(enumConstants);
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = Optional.ofNullable(qualifiedName)
                                   .orElse(this.qualifiedName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = Optional.ofNullable(displayName)
                                 .orElse(this.displayName);
    }

    public void updateEnumConstants(List<String> newConstants) {
        this.enumConstants = Optional.ofNullable(newConstants)
                                   .map(ArrayList::new)
                                   .orElseGet(ArrayList::new);
    }

    @Override
    public String toString() {
        return "EnumDetails[" +
                "qualifiedName='" + qualifiedName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", constants=" + enumConstants.size() +
                ']';
    }

}
