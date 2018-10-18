package com.glovoapp.backender;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;

public class ConfigUtils {
	
	@Value("${backender.box_keywords:}") 
    private String[] needsBox; 
    
    @Value("${backender.bike_range:5}") 
    private Integer bikeRange;
    
    @Value("${backender.distance_splitter:0.5}") 
    private Double distanceSplitter;

	public String[] getNeedsBox() {
		return needsBox;
	}

	public void setNeedsBox(String[] needsBox) {
		this.needsBox = needsBox;
	}

	public Integer getBikeRange() {
		return bikeRange;
	}

	public void setBikeRange(Integer bikeRange) {
		this.bikeRange = bikeRange;
	}

	public Double getDistanceSplitter() {
		return distanceSplitter;
	}

	public void setDistanceSplitter(Double distanceSplitter) {
		this.distanceSplitter = distanceSplitter;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigUtils config = (ConfigUtils) o;
        return Objects.equals(needsBox, config.needsBox) &&
                Objects.equals(bikeRange, config.bikeRange) &&
                Objects.equals(distanceSplitter, config.distanceSplitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(needsBox, bikeRange, distanceSplitter);
    }

    @Override
    public String toString() {
        return "Config Loaded \n{" +
                "    needsBox='" + Arrays.toString(needsBox) + "'" +
                "  , bikeRange=" + bikeRange + " Km" +
                "  , distanceSplitter=" + distanceSplitter +" Km" +
                "}";
    }    

}
