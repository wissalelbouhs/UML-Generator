package org.mql.java.parsers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mql.java.info.AnnotationDetails;
import org.mql.java.info.ClassDetails;
import org.mql.java.info.EnumDetails;
import org.mql.java.info.FieldDetails;
import org.mql.java.info.InterfaceDetails;
import org.mql.java.info.LinkDetails;
import org.mql.java.info.MethodDetails;
import org.mql.java.info.PackageDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLGenerator {
	
	
	public static Document generateXML(PackageDetails rootPackage) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("project");
        doc.appendChild(rootElement);

        createPackageXML(rootPackage, doc, rootElement);
        
        String filePath = "resources/generatedXML/" + rootPackage.getPackageName().replace('.', '/') + ".xml";
        writeXMLToFile(doc, filePath);
        return doc;
    }
	
	
	private static void createPackageXML(PackageDetails packageDetails, Document doc, Element parentElement) {
	    Element packageElement = doc.createElement("package");
	    parentElement.appendChild(packageElement);
	    packageElement.setAttribute("name", packageDetails.getPackageName());
	    
	    processClassDetails(packageDetails, doc, packageElement);
	    processInterfaceDetails(packageDetails, doc, packageElement);
	    processEnumDetails(packageDetails, doc, packageElement);
	    processAnnotationDetails(packageDetails, doc, packageElement);
	    processSubPackageDetails(packageDetails, doc, packageElement);
	}

	private static void processClassDetails(PackageDetails packageDetails, Document doc, Element packageElement) {
	    for (ClassDetails classDetails : packageDetails.getPackageClasses()) {
	        createClassXML(classDetails, doc, packageElement);
	    }
	}

	private static void processInterfaceDetails(PackageDetails packageDetails, Document doc, Element packageElement) {
	    for (InterfaceDetails interfaceDetails : packageDetails.getPackageInterfaces()) {
	        createInterfaceXML(interfaceDetails, doc, packageElement);
	    }
	}

	private static void processEnumDetails(PackageDetails packageDetails, Document doc, Element packageElement) {
	    for (EnumDetails enumDetails : packageDetails.getPackageEnums()) {
	        createEnumXML(enumDetails, doc, packageElement);
	    }
	}

	private static void processAnnotationDetails(PackageDetails packageDetails, Document doc, Element packageElement) {
	    for (AnnotationDetails annotationDetails : packageDetails.getPackageAnnotations()) {
	        createAnnotationXML(annotationDetails, doc, packageElement);
	    }
	}

	private static void processSubPackageDetails(PackageDetails packageDetails, Document doc, Element packageElement) {
	    if (packageDetails.getChildPackages() != null) {
	        for (PackageDetails subPackage : packageDetails.getChildPackages()) {
	            createPackageXML(subPackage, doc, packageElement);
	        }
	    }
	}
	
	//////////////////////////////////////////////////////////
	private static void createClassXML(ClassDetails classDetails, Document doc, Element parentElement) {
	    Element classElement = doc.createElement("class");
	    parentElement.appendChild(classElement);
	    
	    addClassNameElement(classDetails, doc, classElement);
	    processClassRelationships(classDetails, doc, classElement);
	    processImplementedInterfaces(classDetails, doc, classElement);
	    processClassFields(classDetails, doc, classElement);
	    processClassMethods(classDetails, doc, classElement);
	}

	private static void addClassNameElement(ClassDetails classDetails, Document doc, Element classElement) {
	    Element classNameElement = doc.createElement("name");
	    classNameElement.appendChild(doc.createTextNode(classDetails.getClassName()));
	    classElement.appendChild(classNameElement);
	}

	private static void processClassRelationships(ClassDetails classDetails, Document doc, Element classElement) {
	    createRelationshipsXML(classDetails, doc, classElement);
	}

	private static void processImplementedInterfaces(ClassDetails classDetails, Document doc, Element classElement) {
	    if (!classDetails.getImplementedInterfaces().isEmpty()) {
	        Element implementedInterfaces = doc.createElement("implementedInterfaces");
	        classElement.appendChild(implementedInterfaces);
	        
	        for (InterfaceDetails iface : classDetails.getImplementedInterfaces()) {
	            createImplementedInterfaceXML(iface, doc, implementedInterfaces);
	        }
	    }
	}

	private static void processClassFields(ClassDetails classDetails, Document doc, Element classElement) {
	    Element fieldsElement = doc.createElement("fields");
	    classElement.appendChild(fieldsElement);
	    
	    for (FieldDetails field : classDetails.getClassFields()) {
	        createFieldXML(field, doc, fieldsElement);
	    }
	}

	private static void processClassMethods(ClassDetails classDetails, Document doc, Element classElement) {
	    Element methodsElement = doc.createElement("methods");
	    classElement.appendChild(methodsElement);
	    
	    for (MethodDetails method : classDetails.getClassMethods()) {
	        generateMethodXML(method, doc, methodsElement);
	    }
	}
	
	////////////////////////////////////////////////////////////////////////
	
	private static void createInterfaceXML(InterfaceDetails interfaceDetails, Document doc, Element parentElement) {
		   Element interfaceElement = doc.createElement("interface");
		   parentElement.appendChild(interfaceElement);
		   
		   addInterfaceNameElements(interfaceDetails, doc, interfaceElement);
		   addInterfaceModifiersElement(interfaceDetails, doc, interfaceElement);
		   processExtendedClass(interfaceDetails, doc, interfaceElement);
		   processInterfaceFields(interfaceDetails, doc, interfaceElement);
		   processInterfaceMethods(interfaceDetails, doc, interfaceElement);
		}

		private static void addInterfaceNameElements(InterfaceDetails interfaceDetails, Document doc, Element interfaceElement) {
		   Element simpleNameElement = doc.createElement("simpleName");
		   simpleNameElement.appendChild(doc.createTextNode(interfaceDetails.getShortName()));
		   interfaceElement.appendChild(simpleNameElement);
		   
		   Element nameElement = doc.createElement("name");
		   nameElement.appendChild(doc.createTextNode(interfaceDetails.getFullName()));
		   interfaceElement.appendChild(nameElement);
		}

		private static void addInterfaceModifiersElement(InterfaceDetails interfaceDetails, Document doc, Element interfaceElement) {
		   Element modifiersElement = doc.createElement("modifiers");
		   modifiersElement.appendChild(doc.createTextNode(interfaceDetails.getAccessModifiers()));
		   interfaceElement.appendChild(modifiersElement);
		}

		private static void processExtendedClass(InterfaceDetails interfaceDetails, Document doc, Element interfaceElement) {
		   if (interfaceDetails.getParentInterface() != null) {
		       Element extendedClassElement = doc.createElement("extendedClass");
		       extendedClassElement.appendChild(doc.createTextNode(interfaceDetails.getParentInterface()));
		       interfaceElement.appendChild(extendedClassElement);
		   }
		}

		private static void processInterfaceFields(InterfaceDetails interfaceDetails, Document doc, Element interfaceElement) {
		   Element fieldsElement = doc.createElement("fields");
		   interfaceElement.appendChild(fieldsElement);
		   
		   for (FieldDetails field : interfaceDetails.getInterfaceFields()) {
		       createFieldXML(field, doc, fieldsElement);
		   }
		}

		private static void processInterfaceMethods(InterfaceDetails interfaceDetails, Document doc, Element interfaceElement) {
		   Element methodsElement = doc.createElement("methods");
		   interfaceElement.appendChild(methodsElement);
		   
		   for (MethodDetails method : interfaceDetails.getInterfaceMethods()) {
		       generateMethodXML(method, doc, methodsElement);
		   }
		}
		
		////////////////////////////////////////////////////////////////////
		private static void createAnnotationXML(AnnotationDetails annotationDetails, Document doc, Element parentElement) {
			   Element annotationElement = doc.createElement("annotation");
			   parentElement.appendChild(annotationElement);
			   
			   addAnnotationNameElement(annotationDetails, doc, annotationElement);
			   addAnnotationRetentionPolicyElement(annotationDetails, doc, annotationElement);
			   addAnnotationInheritedElement(annotationDetails, doc, annotationElement);
			   processAnnotationAttributes(annotationDetails, doc, annotationElement);
			}

			private static void addAnnotationNameElement(AnnotationDetails annotationDetails, Document doc, Element annotationElement) {
			   Element annotationNameElement = doc.createElement("name");
			   annotationNameElement.appendChild(doc.createTextNode(annotationDetails.getAnnotationName()));
			   annotationElement.appendChild(annotationNameElement);
			}

			private static void addAnnotationRetentionPolicyElement(AnnotationDetails annotationDetails, Document doc, Element annotationElement) {
			   Element retentionElement = doc.createElement("retentionPolicy");
			   retentionElement.appendChild(doc.createTextNode(annotationDetails.getRetentionPolicy().toString()));
			   annotationElement.appendChild(retentionElement);
			}

			private static void addAnnotationInheritedElement(AnnotationDetails annotationDetails, Document doc, Element annotationElement) {
			   Element inheritedElement = doc.createElement("isInherited");
			   inheritedElement.appendChild(doc.createTextNode(String.valueOf(annotationDetails.isInherited())));
			   annotationElement.appendChild(inheritedElement);
			}

			private static void processAnnotationAttributes(AnnotationDetails annotationDetails, Document doc, Element annotationElement) {
			   Element attributesElement = doc.createElement("attributes");
			   annotationElement.appendChild(attributesElement);
			   
			   for (Map.Entry<String, String> attribute : annotationDetails.getAttributes().entrySet()) {
			       Element attributeElement = doc.createElement("attribute");
			       attributesElement.appendChild(attributeElement);
			       
			       Element attributeNameElement = doc.createElement("name");
			       attributeNameElement.appendChild(doc.createTextNode(attribute.getKey()));
			       attributeElement.appendChild(attributeNameElement);
			       
			       Element attributeTypeElement = doc.createElement("type");
			       attributeTypeElement.appendChild(doc.createTextNode(attribute.getValue()));
			       attributeElement.appendChild(attributeTypeElement);
			   }
			}

	/////////////////////////////////////////////////////////////////////////////////////////////
			
			private static void createEnumXML(EnumDetails enumDetails, Document doc, Element parentElement) {
				   Element enumElement = doc.createElement("enum");
				   parentElement.appendChild(enumElement);
				   
				   addEnumNameElement(enumDetails, doc, enumElement);
				   processEnumFields(enumDetails, doc, enumElement);
				}

				private static void addEnumNameElement(EnumDetails enumDetails, Document doc, Element enumElement) {
				   Element enumNameElement = doc.createElement("name");
				   enumNameElement.appendChild(doc.createTextNode(enumDetails.getQualifiedName()));
				   enumElement.appendChild(enumNameElement);
				}

				private static void processEnumFields(EnumDetails enumDetails, Document doc, Element enumElement) {
				   Element fieldsElement = doc.createElement("fields");
				   enumElement.appendChild(fieldsElement);
				   
				   for (String field : enumDetails.getEnumConstants()) {
				       Element fieldElement = doc.createElement("field");
				       fieldElement.appendChild(doc.createTextNode(field));
				       fieldsElement.appendChild(fieldElement);
				   }
				}
		///////////////////////////////////////////////////////////////////////////////////////////////////
				
				private static void createRelationshipsXML(ClassDetails classDetails, Document doc, Element parentElement) {
					   Element relationsElement = doc.createElement("relationships");
					   parentElement.appendChild(relationsElement);
					   
					   processParentRelationship(classDetails, doc, relationsElement);
					   processUsedClassesRelationships(classDetails, doc, relationsElement);
					   processCompositionRelationships(classDetails, doc, relationsElement);
					   processAggregationRelationships(classDetails, doc, relationsElement);
					}

					private static void processParentRelationship(ClassDetails classDetails, Document doc, Element relationsElement) {
					   if (classDetails.getParentClass() != null) {
					       Element inheritanceElement = doc.createElement("parent");
					       inheritanceElement.appendChild(doc.createTextNode(classDetails.getParentClass()));
					       relationsElement.appendChild(inheritanceElement);
					   }
					}

					private static void processUsedClassesRelationships(ClassDetails classDetails, Document doc, Element relationsElement) {
					   for (LinkDetails usedClass : classDetails.getUsageRelations()) {
					       Element usedElement = doc.createElement("uses");
					       usedElement.setAttribute("from", usedClass.getSimplifiedOrigin());
					       usedElement.setAttribute("to", usedClass.getSimplifiedDestination());
					       relationsElement.appendChild(usedElement);
					   }
					}

					private static void processCompositionRelationships(ClassDetails classDetails, Document doc, Element relationsElement) {
					   for (LinkDetails composedClass : classDetails.getCompositionRelations()) {
					       Element compositionElement = doc.createElement("composition");
					       compositionElement.setAttribute("from", composedClass.getSimplifiedOrigin());
					       compositionElement.setAttribute("to", composedClass.getSimplifiedDestination());
					       compositionElement.setAttribute("maxOccurs", composedClass.getMaxCardinality());
					       relationsElement.appendChild(compositionElement);
					   }
					}

					private static void processAggregationRelationships(ClassDetails classDetails, Document doc, Element relationsElement) {
					   for (LinkDetails aggregatedClass : classDetails.getAggregationRelations()) {
					       Element aggregationElement = doc.createElement("aggregation");
					       aggregationElement.setAttribute("from", aggregatedClass.getSimplifiedOrigin());
					       aggregationElement.setAttribute("to", aggregatedClass.getSimplifiedDestination());
					       aggregationElement.setAttribute("maxOccurs", aggregatedClass.getMaxCardinality());
					       relationsElement.appendChild(aggregationElement);
					   }
					}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
					private static void createImplementedInterfaceXML(InterfaceDetails interfaceDetails, Document doc, Element parentElement) {
					   Element interfaceElement = doc.createElement("interface");
					   parentElement.appendChild(interfaceElement);
					   interfaceElement.setAttribute("name", interfaceDetails.getShortName());
					}

					private static void createFieldXML(FieldDetails fieldDetails, Document doc, Element parentElement) {
					   Element fieldElement = doc.createElement("field");
					   parentElement.appendChild(fieldElement);
					   
					   addFieldNameElement(fieldDetails, doc, fieldElement);
					   addFieldTypeElement(fieldDetails, doc, fieldElement);
					   addFieldModifierElement(fieldDetails, doc, fieldElement);
					}

					private static void addFieldNameElement(FieldDetails fieldDetails, Document doc, Element fieldElement) {
					   Element nameElement = doc.createElement("name");
					   nameElement.appendChild(doc.createTextNode(fieldDetails.getFieldName()));
					   fieldElement.appendChild(nameElement);
					}

					private static void addFieldTypeElement(FieldDetails fieldDetails, Document doc, Element fieldElement) {
					   Element typeElement = doc.createElement("type");
					   typeElement.appendChild(doc.createTextNode(fieldDetails.getFieldType()));
					   fieldElement.appendChild(typeElement);
					}

					private static void addFieldModifierElement(FieldDetails fieldDetails, Document doc, Element fieldElement) {
					   Element modifierElement = doc.createElement("modifier");
					   modifierElement.appendChild(doc.createTextNode(String.valueOf(fieldDetails.getAccessModifier())));
					   fieldElement.appendChild(modifierElement);
					}
					
					
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					private static void generateMethodXML(MethodDetails methodDetails, Document document, Element parentElement) {
					    Element methodElement = document.createElement("method");
					    parentElement.appendChild(methodElement);

					    addMethodNameElement(methodDetails, document, methodElement);
					    addMethodReturnTypeElement(methodDetails, document, methodElement);
					    addMethodModifierElement(methodDetails, document, methodElement);
					}


					private static void addMethodNameElement(MethodDetails methodDetails, Document doc, Element methodElement) {
					   Element nameElement = doc.createElement("name");
					   nameElement.appendChild(doc.createTextNode(methodDetails.getMethodName()));
					   methodElement.appendChild(nameElement);
					}

					private static void addMethodReturnTypeElement(MethodDetails methodDetails, Document doc, Element methodElement) {
					   Element returnTypeElement = doc.createElement("returnType");
					   returnTypeElement.appendChild(doc.createTextNode(methodDetails.getReturnType()));
					   methodElement.appendChild(returnTypeElement);
					}

					private static void addMethodModifierElement(MethodDetails methodDetails, Document doc, Element methodElement) {
					   Element modifierElement = doc.createElement("modifier");
					   modifierElement.appendChild(doc.createTextNode(String.valueOf(methodDetails.getAccessModifier())));
					   methodElement.appendChild(modifierElement);
					}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					public static void displayXMLContent(Document xmlDocument) throws TransformerException {
					    Transformer xmlTransformer = createXMLTransformer();
					    DOMSource xmlSource = new DOMSource(xmlDocument);
					    StreamResult consoleOutput = new StreamResult(System.out);
					    xmlTransformer.transform(xmlSource, consoleOutput);
					}

					public static void writeXMLToFile(Document xmlDocument, String filePath) throws TransformerException, IOException {
					    File outputFile = createDirectoriesForFile(filePath);
					    Transformer xmlTransformer = createXMLTransformer();
					    StreamResult fileOutput = new StreamResult(outputFile);
					    xmlTransformer.transform(new DOMSource(xmlDocument), fileOutput);
					    System.out.println("XML file successfully saved to: " + outputFile.getAbsolutePath());
					}

					private static Transformer createXMLTransformer() throws TransformerConfigurationException {
					    TransformerFactory transformerFactory = TransformerFactory.newInstance();
					    Transformer xmlTransformer = transformerFactory.newTransformer();
					    xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
					    return xmlTransformer;
					}

					private static File createDirectoriesForFile(String filePath) {
					    File outputFile = new File(filePath);
					    outputFile.getParentFile().mkdirs();
					    return outputFile;
					}

}
