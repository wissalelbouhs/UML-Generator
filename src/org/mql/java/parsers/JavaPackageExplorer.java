package org.mql.java.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.mql.java.info.AnnotationDetails;
import org.mql.java.info.ClassDetails;
import org.mql.java.info.EnumDetails;
import org.mql.java.info.InterfaceDetails;
import org.mql.java.info.PackageDetails;

public class JavaPackageExplorer {
	
	
	    private String packageName;
	    
	    public JavaPackageExplorer(String packageName) {
	        this.packageName = packageName;
	    }
	
	
	public List<ClassDetails> discoverClassElements() {
	    List<ClassDetails> classCollection = new ArrayList<>();
	    File sourceDirectory = resolveSourceDirectory();
	    
	    if (isValidDirectory(sourceDirectory)) {
	        processDirectoryFiles(sourceDirectory, classCollection);
	    }
	    
	    return classCollection;
	}

	private File resolveSourceDirectory() {
	    String resolvedPath = "src/" + packageName.replace('.', '/');
	    return new File(resolvedPath);
	}

	private boolean isValidDirectory(File directory) {
	    return directory.exists() && directory.isDirectory();
	}

	private void processDirectoryFiles(File directory, List<ClassDetails> classCollection) {
	    File[] potentialFiles = directory.listFiles(this::isJavaSourceFile);
	    
	    if (potentialFiles != null) {
	        Stream.of(potentialFiles)
	            .map(this::extractFullClassName)
	            .map(ClassDetails::new)
	            .forEach(classCollection::add);
	    }
	}

	private boolean isJavaSourceFile(File file) {
	    return file.getName().endsWith(".java");
	}

	private String extractFullClassName(File file) {
	    return packageName + "." + file.getName().replace(".java", "");
	} 
	
	///////////////////////////////////////////////////////////////////
	public List<ClassDetails> explorePackageHierarchy() {
	    File rootDirectory = buildSourcePath();
	    List<ClassDetails> classRegistry = new ArrayList<>();
	    
	    if (isTraversableDirectory(rootDirectory)) {
	        collectClassesRecursively(rootDirectory, packageName, classRegistry);
	    }
	    
	    return classRegistry;
	}

	private void collectClassesRecursively(File currentDirectory, String currentNamespace, List<ClassDetails> classRegistry) {
	    Optional.ofNullable(currentDirectory.listFiles())
	        .ifPresent(directoryContents -> {
	            for (File item : directoryContents) {
	                processFileSystemItem(item, currentNamespace, classRegistry);
	            }
	        });
	}

	private void processFileSystemItem(File item, String currentNamespace, List<ClassDetails> classRegistry) {
	    if (item.isDirectory()) {
	        String expandedNamespace = currentNamespace + "." + item.getName();
	        collectClassesRecursively(item, expandedNamespace, classRegistry);
	    } else if (isJavaSourceFile(item)) {
	        registerClassFromFile(item, currentNamespace, classRegistry);
	    }
	}

	private File buildSourcePath() {
	    return new File("src/" + packageName.replace('.', '/'));
	}

	private boolean isTraversableDirectory(File directory) {
	    return directory.exists() && directory.isDirectory();
	}

	private void registerClassFromFile(File file, String currentNamespace, List<ClassDetails> classRegistry) {
	    String className = currentNamespace + "." + file.getName().replace(".java", "");
	    classRegistry.add(new ClassDetails(className));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	public Map<String, List<ClassDetails>> catalogPackageStructure() {
	    File rootDirectory = buildSourceDirectory();
	    Map<String, List<ClassDetails>> packageRegistry = new HashMap<>();
	    
	    if (isValidTraversalDirectory(rootDirectory)) {
	        mapPackageHierarchy(rootDirectory, packageName, packageRegistry);
	    }
	    
	    return packageRegistry;
	}

	private void mapPackageHierarchy(File currentDirectory, String currentNamespace, 
	                                  Map<String, List<ClassDetails>> packageRegistry) {
	    Optional.ofNullable(currentDirectory.listFiles())
	        .ifPresent(directoryContents -> {
	            for (File item : directoryContents) {
	                processFileSystemMapping(item, currentNamespace, packageRegistry);
	            }
	        });
	}

	private void processFileSystemMapping(File item, String currentNamespace, 
	                                      Map<String, List<ClassDetails>> packageRegistry) {
	    if (item.isDirectory()) {
	        String expandedNamespace = currentNamespace + "." + item.getName();
	        mapPackageHierarchy(item, expandedNamespace, packageRegistry);
	    } else if (isJavaSourceFile(item)) {
	        registerClassToPackage(item, currentNamespace, packageRegistry);
	    }
	}

	private File buildSourceDirectory() {
	    return new File("src/" + packageName.replace('.', '/'));
	}

	private boolean isValidTraversalDirectory(File directory) {
	    return directory.exists() && directory.isDirectory();
	}


	private void registerClassToPackage(File file, String currentNamespace, 
	                                    Map<String, List<ClassDetails>> packageRegistry) {
	    String className = currentNamespace + "." + file.getName().replace(".java", "");
	    packageRegistry
	        .computeIfAbsent(currentNamespace, k -> new ArrayList<>())
	        .add(new ClassDetails(className));
	}
	
	////////////////////////////////////////////////////////////////////////////
	public PackageDetails scanPackage(String packageName) {
	    String path = "src/" + packageName.replace('.', '/');
	    File directory = new File(path);
	    
	    if (directory.exists() && directory.isDirectory()) {
	        PackageDetails packageDetails = new PackageDetails(packageName);
	        
	        List<ClassDetails> classes = new ArrayList<>();
	        List<InterfaceDetails> interfaces = new ArrayList<>();
	        List<AnnotationDetails> annotations = new ArrayList<>();
	        List<EnumDetails> enums = new ArrayList<>();
	        List<PackageDetails> subPackages = new ArrayList<>();
	        
	        for (File file : directory.listFiles()) {
	            if (file.isDirectory()) {
	                String subPackageName = packageName + "." + file.getName();
	                PackageDetails subPackage = scanPackage(subPackageName);
	                if (subPackage != null) {
	                    subPackages.add(subPackage);
	                }
	            } else if (file.getName().endsWith(".java")) {
	                String className = packageName + "." + file.getName().replace(".java", "");
	                String type = detectElementClassification(className);
	                
	                switch (type) {
	                    case "class":
	                        classes.add(new ClassDetails(className));
	                        break;
	                    case "interface":
	                        try {
	                            interfaces.add(new InterfaceDetails(className));
	                        } catch (ClassNotFoundException e) {
	                            e.printStackTrace();
	                        }
	                        break;
	                    case "enum":
	                        enums.add(new EnumDetails(className));
	                        break;
	                    case "annotation":
	                        annotations.add(new AnnotationDetails(className));
	                        break;
	                }
	            }
	        }
	        
	        packageDetails.setPackageClasses(classes);
	        packageDetails.setPackageInterfaces(interfaces);
	        packageDetails.setChildPackages(subPackages);
	        packageDetails.setPackageAnnotations(annotations);
	        packageDetails.setPackageEnums(enums);
	        
	        return packageDetails;
	    }
	    return null;
	}

	public PackageDetails scan() {
	    return scanPackage(packageName);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private String detectElementClassification(String className) {
	    return Optional.ofNullable(loadClassDefinition(className))
	        .map(this::categorizeElementType)
	        .orElse(null);
	}

	private Class<?> loadClassDefinition(String className) {
	    try {
	        return Class.forName(className);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	private String categorizeElementType(Class<?> classDefinition) {
	    if (classDefinition.isEnum()) return "enum";
	    if (classDefinition.isInterface()) return "interface";
	    if (classDefinition.isAnnotation()) return "annotation";
	    return "class";
	}

}
