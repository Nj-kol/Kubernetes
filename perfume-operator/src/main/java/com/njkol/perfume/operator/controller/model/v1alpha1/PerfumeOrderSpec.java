package com.njkol.perfume.operator.controller.model.v1alpha1;

public class PerfumeOrderSpec {

	private String brandName;
	private String perfumeName;
	private String concentration;
	private String orders;
	
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getPerfumeName() {
		return perfumeName;
	}
	public void setPerfumeName(String perfumeName) {
		this.perfumeName = perfumeName;
	}
	public String getConcentration() {
		return concentration;
	}
	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	
	@Override
	public String toString() {
		return "PerfumerOrderSpec [brandName=" + brandName + ", perfumeName=" + perfumeName + ", concentration="
				+ concentration + ", orders=" + orders + "]";
	}

}
