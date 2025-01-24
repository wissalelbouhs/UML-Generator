package org.mql.java.info;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDetails {
	private String className;
    private String fullClassName;
    private String classModifiers;
    private String parentClass;
    private FieldDetails f;
    private List<FieldDetails> classFields;
    private List<MethodDetails> classMethods;
    private List<LinkDetails> classRelations;
    private List<LinkDetails> usageRelations;
    private List<LinkDetails> compositionRelations;
    private List<LinkDetails> aggregationRelations;
    private List<InterfaceDetails> implementedInterfaces;
    
    
    public ClassDetails() {
		
	}
    
    
    public ClassDetails(String path) {
		try {
			Class<?> cls = Class.forName(path);
			className = cls.getSimpleName();
			fullClassName = cls.getName();
			extractClassModifiers(cls);
			extractParentClass(cls);
			extractClassFields(cls);	
			extractClassMethods(cls);
			extractClassRelations(classFields);
			extractUsageRelations(cls);
			getCompositionRelations(classFields);
			getAggregationRelations(classFields);
			getImplementedInterfaces(cls); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
    
    
    private void extractClassMethods(Class<?> cls) {
        classMethods = new ArrayList<MethodDetails>();
        
        for (Method method : cls.getDeclaredMethods()) {
            classMethods.add(new MethodDetails(method));
        }
    }
    
    //////////////////////////////////////////////////

    private void extractClassRelations(List<FieldDetails> classFields) {
        classRelations = new ArrayList<LinkDetails>();
        
        if (parentClass != null) {
            classRelations.add(new LinkDetails(fullClassName, parentClass, "Inheritance"));
        }
    }
    
    //////////////////////////////////////////////////////////
    private void extractClassModifiers(Class<?> cls) {
        classModifiers = Modifier.toString(cls.getModifiers());
    }

    ////////////////////////////////////////////////////
    private void extractClassFields(Class<?> cls) {
        classFields = new ArrayList<FieldDetails>();
        
        for (Field field : cls.getDeclaredFields()) {
            classFields.add(new FieldDetails(field));
        }
    }
    
    ///////////////////////////////////////////////////
    private void extractParentClass(Class<?> cls) {
        Class<?> superClass = cls.getSuperclass();
        
        if (superClass == null) {
            parentClass = null;
            return;
        }
        
        String parentClassName = superClass.getName();
        parentClass = "java.lang.Object".equals(parentClassName) ? null : parentClassName;
    }
    
    /////////////////////////////////////////////////////////////
    private void extractUsageRelations(Class<?> cls) {
        usageRelations = new ArrayList<>();
        Set<String> processedTypes = extractFieldTypes();
        
        processMethodParameters(cls, processedTypes);
        processReturnTypes(cls, processedTypes);
    }

    private Set<String> extractFieldTypes() {
        return classFields.stream()
            .map(FieldDetails::getFieldType) 
            .collect(Collectors.toSet());
    }


    private void processMethodParameters(Class<?> cls, Set<String> processedTypes) {
        for (Method method : cls.getDeclaredMethods()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                addUsageRelation(cls, paramType, processedTypes);
            }
        }
    }

    private void processReturnTypes(Class<?> cls, Set<String> processedTypes) {
        for (Method method : cls.getDeclaredMethods()) {
            Class<?> returnType = method.getReturnType();
            addUsageRelation(cls, returnType, processedTypes);
        }
    }

    private void addUsageRelation(Class<?> cls, Class<?> type, Set<String> processedTypes) {
        String typeName = type.getName();
        
        if (!isValidForUsageRelation(type, typeName, processedTypes)) {
            return;
        }

        LinkDetails relation = new LinkDetails(cls.getName(), typeName, "Use");
        
        if (!usageRelations.contains(relation)) {
            usageRelations.add(relation);
            classRelations.add(relation);
        }
    }

    private boolean isValidForUsageRelation(Class<?> type, String typeName, Set<String> processedTypes) {
        return isExternalType(type) && 
               !processedTypes.contains(typeName) && 
               !hasExistingRelation(typeName);
    }

    private boolean hasExistingRelation(String typeName) {
        return usageRelations.stream()
            .anyMatch(relation -> relation.getDestination().equals(typeName));
    }

    
    ////////////////////////////////////////////////////////////////////
    private void getCompositionRelations(List<FieldDetails> classFields) {
        compositionRelations = new ArrayList<LinkDetails>();
        
        for (FieldDetails field : classFields) {
            if (field.isEstTypeExterne()) {  
                String from = this.fullClassName;
                String to = field.getFieldType();
                LinkDetails relation = new LinkDetails(from, to, "Composition");
                
                if (field.isCollection()) {
                    relation.setMaxCardinality("*");
                } else {
                    relation.setMaxCardinality("1");
                }
                
                compositionRelations.add(relation);
                classRelations.add(relation);
            }
        }
    }
    
    ///////////////////////////////////////////////////////
    private void getAggregationRelations(List<FieldDetails> classFields) {
        aggregationRelations = new ArrayList<>();
        
        for (FieldDetails field : classFields) {
            if (field.isEstTypeExterne()) {
                String from = this.fullClassName;
                String to = field.getFieldType();
                LinkDetails relation = new LinkDetails(from, to, "Aggregation");
                
                if (field.isCollection()) {
                    relation.setMaxCardinality("*");
                } else {
                    relation.setMaxCardinality("1");
                }
                
                aggregationRelations.add(relation);
                classRelations.add(relation);
            }
        }
    }

    ////////////////////////////////////////////////////////////
    private void getImplementedInterfaces(Class<?> cls) {
        implementedInterfaces = new ArrayList<>();
        
        for (Class<?> iface : cls.getInterfaces()) {
            implementedInterfaces.add(new InterfaceDetails(iface));
            LinkDetails relation = new LinkDetails(this.fullClassName, iface.getName(), "Implementation");
            classRelations.add(relation);
        }
    }
    
    
    /////////////////////////////////////////////////////////////
    private boolean relationExists(String destination) {
        for (LinkDetails relation : usageRelations) {
            if (relation.getDestination().equals(destination)) {
                return true;
            }
        }
        return false;
    }

  


    public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getFullClassName() {
		return fullClassName;
	}


	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}


	public String getClassModifiers() {
		return classModifiers;
	}


	public void setClassModifiers(String classModifiers) {
		this.classModifiers = classModifiers;
	}


	public String getParentClass() {
		return parentClass;
	}


	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}


	public List<FieldDetails> getClassFields() {
		return classFields;
	}


	public void setClassFields(List<FieldDetails> classFields) {
		this.classFields = classFields;
	}


	public List<MethodDetails> getClassMethods() {
		return classMethods;
	}


	public void setClassMethods(List<MethodDetails> classMethods) {
		this.classMethods = classMethods;
	}


	public List<LinkDetails> getClassRelations() {
		return classRelations;
	}


	public void setClassRelations(List<LinkDetails> classRelations) {
		this.classRelations = classRelations;
	}


	public List<LinkDetails> getUsageRelations() {
		return usageRelations;
	}


	public void setUsageRelations(List<LinkDetails> usageRelations) {
		this.usageRelations = usageRelations;
	}


	public List<LinkDetails> getCompositionRelations() {
		return compositionRelations;
	}


	public void setCompositionRelations(List<LinkDetails> compositionRelations) {
		this.compositionRelations = compositionRelations;
	}


	public List<LinkDetails> getAggregationRelations() {
		return aggregationRelations;
	}


	public void setAggregationRelations(List<LinkDetails> aggregationRelations) {
		this.aggregationRelations = aggregationRelations;
	}


	public List<InterfaceDetails> getImplementedInterfaces() {
		return implementedInterfaces;
	}


	public void setImplementedInterfaces(List<InterfaceDetails> implementedInterfaces) {
		this.implementedInterfaces = implementedInterfaces;
	}

	  
	private boolean isExternalType(Class<?> cls) {
	    return !isPrimitiveOrWrapper(cls) && !isFromJavaLang(cls);
	}

	private boolean isPrimitiveOrWrapper(Class<?> cls) {
	    return cls.isPrimitive() || isWrapperClass(cls);
	}

	private boolean isWrapperClass(Class<?> cls) {
	    return cls.equals(Boolean.class) || cls.equals(Character.class) || cls.equals(Byte.class) || cls.equals(Short.class) ||
	           cls.equals(Integer.class) || cls.equals(Long.class) || cls.equals(Float.class) || cls.equals(Double.class);
	}

	private boolean isFromJavaLang(Class<?> cls) {
	    return cls.getPackage() != null && cls.getPackage().getName().startsWith("java.lang");
	}

  
}
