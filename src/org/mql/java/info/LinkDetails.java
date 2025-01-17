package org.mql.java.info;



public class LinkDetails {
    private String origin;
    private String simplifiedOrigin;
    private String destination;
    private String simplifiedDestination;
    private String linkType;
    private String minCardinality;
    private String maxCardinality;
    
    public LinkDetails() {
		// TODO Auto-generated constructor stub
	}

    public LinkDetails(String origin, String destination, String linkType) {
        this.origin = origin;
        this.destination = destination;
        this.linkType = linkType;
        
        this.simplifiedOrigin = extractSimpleName(origin);
        this.simplifiedDestination = extractSimpleName(destination);
    }

    private String extractSimpleName(String qualifiedName) {
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        return lastDotIndex != -1 ? qualifiedName.substring(lastDotIndex + 1) : qualifiedName;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getSimplifiedOrigin() {
        return simplifiedOrigin;
    }

    public String getSimplifiedDestination() {
        return simplifiedDestination;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public void setSimplifiedOrigin(String simplifiedOrigin) {
        this.simplifiedOrigin = simplifiedOrigin;
    }

    public void setSimplifiedDestination(String simplifiedDestination) {
        this.simplifiedDestination = simplifiedDestination;
    }

    public String getMinCardinality() {
        return minCardinality;
    }

    public void setMinCardinality(String minCardinality) {
        this.minCardinality = minCardinality;
    }

    public String getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality(String maxCardinality) {
        this.maxCardinality = maxCardinality;
    }
}
