package com.letisim.dto;

import java.util.List;

/** Внутреннее DTO движка LETISim — упрощённое представление BPSim-сценария. */
public class LetisimScenarioDto {

    // Атрибуты <Scenario>
    private String id;
    private String name;
    private String description;
    private String author;
    private String version;

    // Поля из <ScenarioParameters>
    private String baseCurrencyUnit;
    private String baseTimeUnit; // "ms", "s", "min", "hour", "day", "year"
    private Integer replication;
    private Long seed;
    private boolean traceOutput;

    // Агрегат вложенных элементов
    private List<String> calendarNames;
    private int elementParameterCount;
    private List<ElementParameterDto> elementParameters;

    public LetisimScenarioDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getBaseCurrencyUnit() { return baseCurrencyUnit; }
    public void setBaseCurrencyUnit(String baseCurrencyUnit) { this.baseCurrencyUnit = baseCurrencyUnit; }

    public String getBaseTimeUnit() { return baseTimeUnit; }
    public void setBaseTimeUnit(String baseTimeUnit) { this.baseTimeUnit = baseTimeUnit; }

    public Integer getReplication() { return replication; }
    public void setReplication(Integer replication) { this.replication = replication; }

    public Long getSeed() { return seed; }
    public void setSeed(Long seed) { this.seed = seed; }

    public boolean isTraceOutput() { return traceOutput; }
    public void setTraceOutput(boolean traceOutput) { this.traceOutput = traceOutput; }

    public List<String> getCalendarNames() { return calendarNames; }
    public void setCalendarNames(List<String> calendarNames) { this.calendarNames = calendarNames; }

    public int getElementParameterCount() { return elementParameterCount; }
    public void setElementParameterCount(int elementParameterCount) { this.elementParameterCount = elementParameterCount; }

    public List<ElementParameterDto> getElementParameters() { return elementParameters; }
    public void setElementParameters(List<ElementParameterDto> elementParameters) { this.elementParameters = elementParameters; }

    @Override
    public String toString() {
        return "LetisimScenarioDto{id='" + id + "', name='" + name + "', author='" + author +
                "', baseCurrencyUnit='" + baseCurrencyUnit + "', baseTimeUnit='" + baseTimeUnit +
                "', replication=" + replication + ", elementParameterCount=" + elementParameterCount + '}';
    }
}
