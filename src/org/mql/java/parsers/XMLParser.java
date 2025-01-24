package org.mql.java.parsers;

import java.util.ArrayList;
import java.util.List;

import org.mql.java.info.AnnotationDetails;
import org.mql.java.info.ClassDetails;
import org.mql.java.info.EnumDetails;
import org.mql.java.info.FieldDetails;
import org.mql.java.info.InterfaceDetails;
import org.mql.java.info.LinkDetails;
import org.mql.java.info.MethodDetails;
import org.mql.java.info.PackageDetails;

public class XMLParser {
	
	private XMLNode xmlNode;
	
	public XMLParser(String path) {
	    this.xmlNode = new XMLNode(path);
	}

	public PackageDetails parse() {
	    PackageDetails rootPackage = new PackageDetails();        
	    XMLNode mainPackage = xmlNode.firstChild();
	    rootPackage = parseRecursively(mainPackage);
	    
	    return rootPackage;
	}
	
	
	private PackageDetails parseRecursively(XMLNode node) {
	    PackageDetails packageContext = extractPackageMetadata(node);
	    
	    List<PackageDetails> childPackageCollection = extractChildPackages(node);
	    List<ClassDetails> classCollection = extractPackageClasses(node);
	    
	    packageContext.setChildPackages(childPackageCollection);
	    packageContext.setPackageClasses(classCollection);
	    
	    return packageContext;
	}

	private PackageDetails extractPackageMetadata(XMLNode node) {
	    PackageDetails packageDetails = new PackageDetails();
	    String packageName = node.getAttribute("name");
	    packageDetails.setPackageName(packageName);
	    return packageDetails;
	}

	private List<PackageDetails> extractChildPackages(XMLNode node) {
	    List<PackageDetails> childPackages = new ArrayList<>();
	    List<XMLNode> children = node.children() != null ? node.children() : new ArrayList<>();
	    
	    for (XMLNode child : children) {
	        if (child.getName().equals("package")) {
	            childPackages.add(parseRecursively(child));
	        }
	    }
	    
	    return childPackages;
	}

	private List<ClassDetails> extractPackageClasses(XMLNode node) {
	    List<ClassDetails> packageClasses = new ArrayList<>();
	    List<XMLNode> children = node.children() != null ? node.children() : new ArrayList<>();
	    
	    for (XMLNode child : children) {
	        if (child.getName().equals("class")) {
	            ClassDetails cls = generateClassStructure(child);
	            packageClasses.add(cls);
	        }
	    }
	    
	    return packageClasses;
	}
	
	////////////////////////////////////////////////////////
	
	private ClassDetails generateClassStructure(XMLNode node) {
	    ClassDetails classDetails = initializeClassBasicInfo(node);
	    
	    processClassFields(node, classDetails);
	    processClassMethods(node, classDetails);
	    processClassLinks(node, classDetails);
	    processImplementedInterfaces(node, classDetails);
	    
	    return classDetails;
	}

	private ClassDetails initializeClassBasicInfo(XMLNode node) {
	    ClassDetails classDetails = new ClassDetails();
	    classDetails.setClassName(node.child("name").getValue());
	    return classDetails;
	}

	private void processClassFields(XMLNode node, ClassDetails classDetails) {
	    List<FieldDetails> classFields = new ArrayList<>();
	    XMLNode fieldsNode = node.child("fields");
	    List<XMLNode> fields = fieldsNode != null ? fieldsNode.children() : new ArrayList<>();
	    
	    for (XMLNode field : fields) {
	        FieldDetails fieldInfo = new FieldDetails();
	        fieldInfo.setFieldName(field.child("name").getValue());
	        fieldInfo.setFieldType(field.child("type").getValue());
	        fieldInfo.setAccessModifier(String.valueOf(field.child("modifier").getValue().charAt(0)));
	        classFields.add(fieldInfo);
	    }
	    
	    classDetails.setClassFields(classFields);
	}

	private void processClassMethods(XMLNode node, ClassDetails classDetails) {
	    List<MethodDetails> classMethods = new ArrayList<>();
	    XMLNode methodsNode = node.child("methods");
	    List<XMLNode> methods = methodsNode != null ? methodsNode.children() : new ArrayList<>();
	    
	    for (XMLNode method : methods) {
	        MethodDetails methodInfo = new MethodDetails();
	        methodInfo.setMethodName(method.child("name").getValue());
	        methodInfo.setReturnType(method.child("returnType").getValue());
	        methodInfo.setAccessModifier(String.valueOf(method.child("modifier").getValue().charAt(0)));
	        classMethods.add(methodInfo);
	    }
	    
	    classDetails.setClassMethods(classMethods);
	}

	private void processClassLinks(XMLNode node, ClassDetails classDetails) {
	    List<LinkDetails> linksList = new ArrayList<>();
	    List<LinkDetails> composedLinks = new ArrayList<>();
	    List<LinkDetails> aggregatedLinks = new ArrayList<>();
	    
	    XMLNode relationsNode = node.child("relationships");
	    List<XMLNode> relations = relationsNode != null ? relationsNode.children() : new ArrayList<>();
	    
	    for (XMLNode relation : relations) {
	        if (!relation.getName().equals("parent")) {
	            LinkDetails linkInfo = new LinkDetails();
	            linkInfo.setOrigin(relation.getAttribute("from"));
	            linkInfo.setDestination(relation.getAttribute("to"));
	            linkInfo.setLinkType(relation.getName());
	            
	            linksList.add(linkInfo);
	            
	            if (relation.getName().equals("aggregation")) {
	                aggregatedLinks.add(linkInfo);
	            } else {
	                composedLinks.add(linkInfo);
	            }
	        } else {
	            classDetails.setParentClass(relation.getValue());
	        }
	    }
	    
	    classDetails.setClassRelations(linksList);
	    classDetails.setCompositionRelations(composedLinks);
	    classDetails.setAggregationRelations(aggregatedLinks);
	}

	private void processImplementedInterfaces(XMLNode node, ClassDetails classDetails) {
	    List<InterfaceDetails> implementedInterfaces = new ArrayList<>();
	    XMLNode implementedInterfacesNode = node.child("implementedInterfaces");
	    
	    List<XMLNode> interfaces = implementedInterfacesNode != null ? 
	        implementedInterfacesNode.children() : new ArrayList<>();
	    
	    for (XMLNode interfaceNode : interfaces) {
	        InterfaceDetails interfaceInfo = new InterfaceDetails();
	        interfaceInfo.setFullName(interfaceNode.getAttribute("name"));
	        implementedInterfaces.add(interfaceInfo);
	    }
	    
	    classDetails.setImplementedInterfaces(implementedInterfaces);
	}

	
	
	
	

}
