package com.glovoapp.backender;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtils {
	
	@Value("${backender.box_keywords:}") 
    private String[] needsBox; 
    
    @Value("${backender.bike_range:5}") 
    private Double bikeRange;
    
    @Value("${backender.distance_splitter:0.5}") 
    private Double distanceSplitter;
    
    @Value("${backender.sort_order}") 
    private String[] sortOrder;

	public String[] getNeedsBox() {
		return needsBox;
	}

	public void setNeedsBox(String[] needsBox) {
		this.needsBox = needsBox;
	}

	public Double getBikeRange() {
		return bikeRange;
	}

	public void setBikeRange(Double bikeRange) {
		this.bikeRange = bikeRange;
	}

	public Double getDistanceSplitter() {
		return distanceSplitter;
	}

	public void setDistanceSplitter(Double distanceSplitter) {
		this.distanceSplitter = distanceSplitter;
	}
	
	public String[] getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String[] sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigUtils config = (ConfigUtils) o;
        return Objects.equals(needsBox, config.needsBox) &&
                Objects.equals(bikeRange, config.bikeRange) &&
                Objects.equals(distanceSplitter, config.distanceSplitter) &&
                Objects.equals(sortOrder, config.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(needsBox, bikeRange, distanceSplitter);
    }

    @Override
    public String toString() {
        return "Config Loaded: \n{" +
                " needsBox='" + Arrays.toString(needsBox) + "'" +
                " , bikeRange=" + bikeRange + " Km" +
                " , distanceSplitter=" + distanceSplitter +" Km" +
                " , sortOrder='" + Arrays.toString(sortOrder) + "'" +
                "}";
    }

}
