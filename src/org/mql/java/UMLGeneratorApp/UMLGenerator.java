package org.mql.java.UMLGeneratorApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import org.mql.java.info.ClassDetails;
import org.mql.java.info.PackageDetails;

import org.mql.java.parsers.JavaPackageExplorer;
import org.mql.java.parsers.XMLGenerator;
import org.mql.java.parsers.XMLParser;
import org.mql.java.ui.Diagram;
import org.w3c.dom.Document;


public class UMLGenerator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UML Diagram Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            String packageName = "org.mql.java";
            JavaPackageExplorer scanner = new JavaPackageExplorer(packageName);
            PackageDetails basePackage = scanner.scan();
            System.out.println(basePackage);
            
            Map<String, List<ClassDetails>> map = scanner.catalogPackageStructure();
            
            JPanel parentPanel = new JPanel();
            parentPanel.setLayout(new GridLayout(0, 2, 200, 200));

            map.forEach((name, classes) -> {
                Diagram diagramPanel = new Diagram(name, classes);

                diagramPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.BLACK, 2),
                    new EmptyBorder(10, 10, 10, 10) 
                ));

                parentPanel.add(diagramPanel);
            });


            
            JScrollPane scrollPane = new JScrollPane(parentPanel);
            frame.add(scrollPane);
            frame.setVisible(true);
        });  
        try {
            PackageDetails rootPackage = new JavaPackageExplorer("org.mql.java").scan();

            Document xmlDocument = XMLGenerator.generateXML(rootPackage);

            XMLGenerator.displayXMLContent(xmlDocument);
            
            PackageDetails mypkg = new XMLParser("resources/generatedXML/java.xml").parse();
            System.out.println(mypkg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
        
    }