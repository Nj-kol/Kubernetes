package com.njkol.perfume.operator.controller.model.v1alpha1;

public class PerfumeOrderStatus {

    private int availableUnits;
    
    public int getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    @Override
    public String toString() {
        return "PerfumeOrderStatus{ availableUnits=" + availableUnits + "}";
    }
}
