package com.letisim.dto;

/**
 * DTO для параметров элемента процесса (задачи или шлюза).
 *
 * <p>Содержит данные, извлечённые из {@code <ElementParameters>} BPSim:
 * временные параметры (распределение), стоимость и вероятности.</p>
 */
public class ElementParameterDto {

    private String id;
    private String elementRef;

    // TimeParameters → ProcessingTime
    private String distributionType;   // "NormalDistribution", "UniformDistribution", etc.
    private Double distributionMean;
    private Double distributionStdDev;
    private Double distributionMin;
    private Double distributionMax;

    // CostParameters → UnitCost
    private Double unitCost;

    // ControlParameters → Probability
    private Double probability;

    // ResourceParameters
    private Double resourceQuantity;
    private String resourceRole;

    public ElementParameterDto() {}

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getElementRef() { return elementRef; }
    public void setElementRef(String elementRef) { this.elementRef = elementRef; }

    public String getDistributionType() { return distributionType; }
    public void setDistributionType(String distributionType) { this.distributionType = distributionType; }

    public Double getDistributionMean() { return distributionMean; }
    public void setDistributionMean(Double distributionMean) { this.distributionMean = distributionMean; }

    public Double getDistributionStdDev() { return distributionStdDev; }
    public void setDistributionStdDev(Double distributionStdDev) { this.distributionStdDev = distributionStdDev; }

    public Double getDistributionMin() { return distributionMin; }
    public void setDistributionMin(Double distributionMin) { this.distributionMin = distributionMin; }

    public Double getDistributionMax() { return distributionMax; }
    public void setDistributionMax(Double distributionMax) { this.distributionMax = distributionMax; }

    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public Double getResourceQuantity() { return resourceQuantity; }
    public void setResourceQuantity(Double resourceQuantity) { this.resourceQuantity = resourceQuantity; }

    public String getResourceRole() { return resourceRole; }
    public void setResourceRole(String resourceRole) { this.resourceRole = resourceRole; }

    @Override
    public String toString() {
        return "ElementParameterDto{id='" + id + "', elementRef='" + elementRef +
                "', distribution=" + distributionType +
                "(mean=" + distributionMean + ", stdDev=" + distributionStdDev + ")" +
                ", unitCost=" + unitCost + ", probability=" + probability + 
                ", role='" + resourceRole + "', quantity=" + resourceQuantity + '}';
    }
}
