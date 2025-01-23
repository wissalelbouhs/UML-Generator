package org.mql.java.info;

import java.util.List;

public class PackageDetails {
    private String packageName;
    private List<PackageDetails> childPackages;
    private List<ClassDetails> packageClasses;
    private List<InterfaceDetails> packageInterfaces;
    private List<AnnotationDetails> packageAnnotations;
    private List<EnumDetails> packageEnums;
    
    public PackageDetails() {
    }
    
    public PackageDetails(String packagePath) {
        String[] segments = packagePath.split("\\.");
        packageName = segments[segments.length-1];
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public List<PackageDetails> getChildPackages() {
        return childPackages;
    }
    
    public void setChildPackages(List<PackageDetails> childPackages) {
        this.childPackages = childPackages;
    }
    
    public List<ClassDetails> getPackageClasses() {
        return packageClasses;
    }
    
    public void setPackageClasses(List<ClassDetails> packageClasses) {
        this.packageClasses = packageClasses;
    }
    
    public List<InterfaceDetails> getPackageInterfaces() {
        return packageInterfaces;
    }
    
    public void setPackageInterfaces(List<InterfaceDetails> packageInterfaces) {
        this.packageInterfaces = packageInterfaces;
    }
    
    public List<AnnotationDetails> getPackageAnnotations() {
        return packageAnnotations;
    }
    
    public void setPackageAnnotations(List<AnnotationDetails> packageAnnotations) {
        this.packageAnnotations = packageAnnotations;
    }
    
    public List<EnumDetails> getPackageEnums() {
        return packageEnums;
    }
    
    public void setPackageEnums(List<EnumDetails> packageEnums) {
        this.packageEnums = packageEnums;
    }
    
    @Override
    public String toString() {
        return "PackageDetails{" +
               "packageName='" + packageName + '\'' +
               ", childPackages=" + childPackages +
               ", packageClasses=" + packageClasses +
               ", packageInterfaces=" + packageInterfaces +
               ", packageAnnotations=" + packageAnnotations +
               ", packageEnums=" + packageEnums +
               '}';
    }
}