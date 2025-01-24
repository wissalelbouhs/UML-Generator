package org.mql.java.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.mql.java.info.ClassDetails;
import org.mql.java.info.FieldDetails;
import org.mql.java.info.LinkDetails;
import org.mql.java.info.MethodDetails;

public class Diagram extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ClassDetails> classes;
    private Map<String, Point> classLocations;
	
	public Diagram(String name, List<ClassDetails> classes) {
        this.classes = classes;
        this.classLocations = new HashMap<>();
        setPreferredSize(new Dimension(1000, 1000));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(new BorderLayout());
        JLabel label = new JLabel(name, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);
    }
	///////////////////////////////////////////////////////////////////////////
	@Override
	protected void paintComponent(Graphics g) {
		   super.paintComponent(g);
		   Graphics2D g2 = setupGraphics(g);
		   
		   int centerX = getWidth() / 2;
		   int centerY = getHeight() / 2;
		   int radius = Math.min(getWidth(), getHeight()) / 3;
		   int numClasses = classes.size();
		   double angleStep = 2 * Math.PI / numClasses;
		   
		   positionAndDrawClasses(g2, centerX, centerY, radius, angleStep);
		  
		}

		private Graphics2D setupGraphics(Graphics g) {
		   Graphics2D g2 = (Graphics2D) g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		   return g2;
		}

		private void positionAndDrawClasses(Graphics2D g2, int centerX, int centerY, int radius, double angleStep) {
		   for (int i = 0; i < classes.size(); i++) {
		       double angle = i * angleStep;
		       Point location = calculateClassLocation(centerX, centerY, radius, angle);
		       classLocations.put(classes.get(i).getClassName(), location);
		       visualizeClass(g2, classes.get(i), location);
		   }
		   visualizeLink(g2);
		   
		}

		private Point calculateClassLocation(int centerX, int centerY, int radius, double angle) {
		   int x = (int) (centerX + radius * Math.cos(angle)) - 100; 
		   int y = (int) (centerY + radius * Math.sin(angle)) - 50; 
		   return new Point(x, y);
		}
	
	////////////////////////////////////////////////////////////////////////////
	private void visualizeClass(Graphics2D g2, ClassDetails cls, Point location) {
		   int width = 150;
		   int x = location.x, y = location.y;
		   
		   drawClassHeader(g2, cls, x, y, width);
		   int totalHeight = drawClassSections(g2, cls, x, y, width);
		   drawClassBorder(g2, x, y, width, totalHeight);
		}

		private void drawClassHeader(Graphics2D g2, ClassDetails cls, int x, int y, int width) {
		   int height = 30;
		   g2.setColor(new Color(60, 150, 18));
		   g2.fillRect(x, y, width, height);
		   g2.setColor(Color.WHITE);
		   g2.setFont(new Font("SansSerif", Font.BOLD, 12));
		   g2.drawString(cls.getClassName(), x + 10, y + 20);
		}

		private int drawClassSections(Graphics2D g2, ClassDetails cls, int x, int y, int width) {
		   int headerHeight = 30;
		   int fieldStartY = y + headerHeight;
		   int fieldHeight = visualizeFields(g2, cls.getClassFields(), x, fieldStartY, width);
		   
		   int methodStartY = fieldStartY + fieldHeight;
		   int methodHeight = visualizeMethods(g2, cls.getClassMethods(), x, methodStartY, width);
		   
		   return headerHeight + fieldHeight + methodHeight;
		}

		private void drawClassBorder(Graphics2D g2, int x, int y, int width, int totalHeight) {
		   g2.setColor(Color.BLACK);
		   g2.drawRect(x, y, width, totalHeight);
		}
	//////////////////////////////////////////////////////////////////
	private int visualizeFields(Graphics2D g2, List<FieldDetails> fields, int x, int y, int width) {
		   int fieldHeight = calculateFieldHeight(fields);
		   renderFieldBackground(g2, x, y, width, fieldHeight);
		   renderFieldBorder(g2, x, y, width, fieldHeight);
		   
		   return renderFieldList(g2, fields, x, y);
		}

		private int calculateFieldHeight(List<FieldDetails> fields) {
		   return fields.size() * 15;
		}

		private void renderFieldBackground(Graphics2D g2, int x, int y, int width, int height) {
		   g2.setColor(Color.WHITE);
		   g2.fillRect(x, y, width, height);
		}

		private void renderFieldBorder(Graphics2D g2, int x, int y, int width, int height) {
		   g2.setColor(Color.BLACK);
		   g2.drawRect(x, y, width, height);
		}

		private int renderFieldList(Graphics2D g2, List<FieldDetails> fields, int x, int y) {
		   g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
		   int currentY = y + 12;
		   
		   for (FieldDetails field : fields) {
		       g2.drawString(field.toStringRepresentation(), x + 5, currentY);
		       currentY += 15;
		   }
		   
		   return fields.size() * 15;
		}
	
	////////////////////////////////////////////////////////////
	private int visualizeMethods(Graphics2D g2, List<MethodDetails> methods, int x, int y, int width) {
		   return renderMethodList(g2, methods, x, y);
		}

		private int renderMethodList(Graphics2D g2, List<MethodDetails> methods, int x, int y) {
		   int methodHeight = methods.size() * 15;
		   int currentY = y + 12;
		   
		   configureMethodFont(g2);
		   drawMethodStrings(g2, methods, x, currentY);
		   
		   return methodHeight;
		}

		private void configureMethodFont(Graphics2D g2) {
		   g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
		}

		private void drawMethodStrings(Graphics2D g2, List<MethodDetails> methods, int x, int currentY) {
		   for (MethodDetails method : methods) {
		       g2.drawString(method.getRepresentation(), x + 5, currentY);
		       currentY += 15;
		   }
		}
	
	///////////////////////////////////////////////////////////
	private void visualizeLink(Graphics2D g2) {
		   for (ClassDetails cls : classes) {
		       drawClassRelationships(g2, cls);
		   }
		}

		private void drawClassRelationships(Graphics2D g2,ClassDetails cls) {
		   for (LinkDetails rel : cls.getClassRelations()) {
		       drawSingleRelationship(g2, rel);
		   }
		}

		private void drawSingleRelationship(Graphics2D g2, LinkDetails rel) {
		   Point from = classLocations.get(rel.getSimplifiedOrigin());
		   Point to = classLocations.get(rel.getSimplifiedDestination());
		   
		   if (from != null && to != null) {
			   visualizeLinkLine(g2, from, to, rel.getLinkType());
		   }
		}
	/////////////////////////////////////////////////////////////////////
	private void visualizeLinkLine(Graphics2D g2, Point from, Point to, String relationType) {
		   int width = 150;
		   int height = 80;
		   Point start = calculateConnectionPoint(from, to, width, height);
		   Point end = calculateConnectionPoint(to, from, width, height);
		   g2.drawLine(start.x, start.y, end.x, end.y);
		   drawRelationMarker(g2, start, end, relationType);
		}

		private void drawRelationMarker(Graphics2D g2, Point start, Point end, String relationType) {
		   switch (relationType) {
		       case "Inheritance":
		    	   visualizeArrow(g2, start, end);
		           break;
		       case "Aggregation":
		    	   visualizeDiamond(g2, start, end, false);
		           break;
		       case "Composition":
		    	   visualizeDiamond(g2, start, end, true);
		           break;
		       case "Implementation":
		           visualizeDashedLine(g2, start, end);
		           visualizeHollowArrow(g2, start, end);
		           break;
		   }
		}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Point calculateConnectionPoint(Point source, Point target, int width, int height) {
	    int x = source.x + width / 2;
	    int y = source.y + height / 2;
	    if (!isVerticalOverlap(source, target, height)) {
	        y = (source.y < target.y) ? source.y + height : source.y;
	    }

	    if (!isHorizontalOverlap(source, target, width)) {
	        x = (source.x < target.x) ? source.x + width : source.x;
	    }

	    return new Point(x, y);
	}
	private boolean isVerticalOverlap(Point source, Point target, int height) {
	    return !(source.y + height < target.y || source.y > target.y + height);
	}

	private boolean isHorizontalOverlap(Point source, Point target, int width) {
	    return !(source.x + width < target.x || source.x > target.x + width);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	private void visualizeDiamond(Graphics2D g2, Point from, Point to, boolean filled) {
	    Point diamondCenter = calculateDiamondCenter(from, to);
	    double angle = calculateAngle(from, to);
	    Polygon diamond = createDiamondPolygon(diamondCenter);
	    Shape rotatedDiamond = rotateShape(diamond, angle, diamondCenter);
	    
	    renderDiamond(g2, rotatedDiamond, filled);
	}

	private Point calculateDiamondCenter(Point from, Point to) {
	    int diamondSize = 10;
	    double dx = to.x - from.x;
	    double dy = to.y - from.y;
	    double length = Math.sqrt(dx * dx + dy * dy);
	    double unitDx = dx / length;
	    double unitDy = dy / length;
	    
	    int centerX = from.x + (int) (unitDx * diamondSize);
	    int centerY = from.y + (int) (unitDy * diamondSize);
	    
	    return new Point(centerX, centerY);
	}

	private double calculateAngle(Point from, Point to) {
	    double dx = to.x - from.x;
	    double dy = to.y - from.y;
	    return Math.atan2(dy, dx);
	}

	private Polygon createDiamondPolygon(Point center) {
	    int diamondSize = 10;
	    int centerX = center.x;
	    int centerY = center.y;
	    
	    return new Polygon(
	        new int[]{centerX, centerX - diamondSize, centerX, centerX + diamondSize},
	        new int[]{centerY - diamondSize, centerY, centerY + diamondSize, centerY},
	        4
	    );
	}

	private Shape rotateShape(Shape shape, double angle, Point center) {
	    AffineTransform transform = new AffineTransform();
	    transform.setToRotation(angle, center.x, center.y);
	    return transform.createTransformedShape(shape);
	}

	private void renderDiamond(Graphics2D g2, Shape diamond, boolean filled) {
	    if (filled) {
	        g2.setColor(Color.BLACK);
	        g2.fill(diamond);
	    } else {
	        g2.setColor(Color.WHITE);
	        g2.fill(diamond);
	    }
	    g2.setColor(Color.BLACK);
	    g2.draw(diamond);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	private void visualizeDashedLine(Graphics2D g2, Point start, Point end) {
	    Stroke dashedStroke = createDashedStroke();
	    drawLineWithStroke(g2, start, end, dashedStroke);
	}

	private Stroke createDashedStroke() {
	    float[] dashPattern = {10.0f, 10.0f};
	    return new BasicStroke(
	        2.0f, 
	        BasicStroke.CAP_BUTT, 
	        BasicStroke.JOIN_MITER, 
	        10.0f, 
	        dashPattern, 
	        0.0f
	    );
	}

	private void drawLineWithStroke(Graphics2D g2, Point start, Point end, Stroke stroke) {
	    Stroke originalStroke = g2.getStroke();
	    g2.setStroke(stroke);
	    g2.drawLine(start.x, start.y, end.x, end.y);
	    g2.setStroke(originalStroke);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	private void visualizeArrow(Graphics2D g2, Point start, Point end) {
	    Polygon arrowHead = createArrowHead(start, end);
	    g2.fillPolygon(arrowHead);
	}

	private Polygon createArrowHead(Point start, Point end) {
	    int arrowSize = 20;
	    double angle = calculateLineAngle(start, end);
	    
	    int[] xPoints = calculateArrowXPoints(end, angle, arrowSize);
	    int[] yPoints = calculateArrowYPoints(end, angle, arrowSize);
	    
	    return new Polygon(xPoints, yPoints, 3);
	}

	private double calculateLineAngle(Point start, Point end) {
	    return Math.atan2(end.y - start.y, end.x - start.x);
	}

	private int[] calculateArrowXPoints(Point end, double angle, int arrowSize) {
	    int x1 = end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
	    int x2 = end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
	    return new int[]{end.x, x1, x2};
	}

	private int[] calculateArrowYPoints(Point end, double angle, int arrowSize) {
	    int y1 = end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
	    int y2 = end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));
	    return new int[]{end.y, y1, y2};
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	private void visualizeHollowArrow(Graphics2D g2, Point start, Point end) {
	    int[] arrowPoints = calculateArrowPoints(start, end);
	    renderHollowArrow(g2, end, arrowPoints);
	}

	private int[] calculateArrowPoints(Point start, Point end) {
	    int arrowSize = 10;
	    double angle = Math.atan2(end.y - start.y, end.x - start.x);
	    
	    int x1 = end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
	    int y1 = end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
	    int x2 = end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
	    int y2 = end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));
	    
	    return new int[]{x1, y1, x2, y2};
	}

	private void renderHollowArrow(Graphics2D g2, Point end, int[] points) {
	    g2.setColor(Color.BLACK);
	    g2.drawLine(end.x, end.y, points[0], points[1]);
	    g2.drawLine(end.x, end.y, points[2], points[3]);
	}

	
	
	

}