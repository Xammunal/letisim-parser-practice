package com.letisim;

import com.letisim.dto.ElementParameterDto;
import com.letisim.dto.LetisimScenarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LetisimEngineMock {
    private static final Logger log = LoggerFactory.getLogger(LetisimEngineMock.class);
    private static final String CSV_FILE_PATH = "credit_card_application_heatmap.csv";
    private final Random random = new Random();

    private static class ElementStats {
        double totalTime = 0.0;
        double totalCost = 0.0;
        int executionCount = 0;
    }

    public void simulateExecution(LetisimScenarioDto scenario) {
        log.info("[LETISim Engine] Генерация Heatmap CSV для сценария: '{}'", scenario.getName());

        int replications = scenario.getReplication() != null ? scenario.getReplication() : 100;
        if (scenario.getSeed() != null) {
            random.setSeed(scenario.getSeed());
        }

        Map<String, ElementStats> statsMap = new HashMap<>();

        for (int i = 0; i < replications; i++) {
            if (scenario.getElementParameters() != null) {
                for (ElementParameterDto ep : scenario.getElementParameters()) {
                    String elementId = ep.getElementRef() != null ? ep.getElementRef() : ep.getId();
                    if (elementId == null || elementId.isEmpty()) continue;

                    statsMap.putIfAbsent(elementId, new ElementStats());
                    ElementStats stats = statsMap.get(elementId);

                    if (ep.getProbability() != null && random.nextDouble() > ep.getProbability()) {
                        continue;
                    }

                    stats.executionCount++;

                    if ("NormalDistribution".equals(ep.getDistributionType()) && ep.getDistributionMean() != null) {
                        double stdDev = ep.getDistributionStdDev() != null ? ep.getDistributionStdDev() : 0.0;
                        stats.totalTime += Math.max(0, random.nextGaussian() * stdDev + ep.getDistributionMean());
                    } else if (ep.getDistributionMean() != null) {
                        stats.totalTime += ep.getDistributionMean();
                    }

                    if (ep.getUnitCost() != null) {
                        stats.totalCost += ep.getUnitCost();
                    }
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.println("elementid,executiontime,cost,resourceutilization");

            for (Map.Entry<String, ElementStats> entry : statsMap.entrySet()) {
                String elementId = entry.getKey();
                ElementStats stats = entry.getValue();

                if (stats.executionCount > 0) {
                    int utilization = (int) Math.round(((double) stats.executionCount / replications) * 100);
                    utilization = Math.min(utilization, 100);

                    writer.printf(java.util.Locale.US, "%s,%.0f,%.0f,%d%n",
                            elementId, stats.totalTime, stats.totalCost, utilization);
                }
            }
            log.info("[LETISim Engine] Результаты сохранены в файл: {}", CSV_FILE_PATH);
        } catch (Exception e) {
            log.error("Ошибка при записи Heatmap CSV", e);
        }
    }
}