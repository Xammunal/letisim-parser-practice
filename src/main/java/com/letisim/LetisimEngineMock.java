package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LetisimEngineMock {
    private static final Logger log = LoggerFactory.getLogger(LetisimEngineMock.class);

    public void simulateExecution(LetisimScenarioDto scenario) {
        System.out.println("======================================================");
        log.info("[LETISim Engine] 🔥 Инициализация ядра симуляции...");
        log.info("[LETISim Engine] Подключение параметров BPSim для сценария: '{}' (ID: {})", scenario.getName(), scenario.getId());

        if (scenario.getAuthor() != null) {
            log.info("[LETISim Engine] Автор сценария: {}", scenario.getAuthor());
        }

        // Эмуляция применения параметров
        String timeUnit = scenario.getBaseTimeUnit() != null ? scenario.getBaseTimeUnit() : "сек";
        log.info("[LETISim Engine] ⏱ Установка базовой единицы времени: {}", timeUnit.toUpperCase());

        String currency = scenario.getBaseCurrencyUnit() != null ? scenario.getBaseCurrencyUnit() : "Не задано";
        log.info("[LETISim Engine] 💰 Установка базовой валюты: {}", currency);

        int replications = scenario.getReplication() != null ? scenario.getReplication() : 1;
        log.info("[LETISim Engine] 🔄 Настройка количества прогонов (репликаций): {}", replications);

        if (scenario.getSeed() != null) {
            log.info("[LETISim Engine] 🎲 Установка Seed для генератора случайных чисел: {}", scenario.getSeed());
        }

        if (scenario.isTraceOutput()) {
            log.info("[LETISim Engine] 📝 Включен режим трассировки вывода (Trace Output).");
        }

        log.info("[LETISim Engine] ⚙️ Применение параметров элементов (найдено: {} элементов)", scenario.getElementParameterCount());
        if (scenario.getElementParameters() != null) {
            for (com.letisim.dto.ElementParameterDto ep : scenario.getElementParameters()) {
                StringBuilder details = new StringBuilder();
                if (ep.getDistributionType() != null) {
                    details.append(String.format("Время: %s (Mean: %.1f) ", ep.getDistributionType(), ep.getDistributionMean() != null ? ep.getDistributionMean() : 0.0));
                }
                if (ep.getUnitCost() != null) {
                    details.append(String.format("Стоимость: %.2f ", ep.getUnitCost()));
                }
                if (ep.getProbability() != null) {
                    details.append(String.format("Вероятность: %.2f ", ep.getProbability()));
                }
                if (ep.getResourceRole() != null) {
                    details.append(String.format("Ресурс: %s (Кол-во: %.1f) ", ep.getResourceRole(), ep.getResourceQuantity() != null ? ep.getResourceQuantity() : 1.0));
                }
                
                log.info("  └─ Элемент [{}]: {}", ep.getElementRef() != null ? ep.getElementRef() : ep.getId(), details.toString());
            }
        }

        // Эмуляция "долгой" работы движка
        try {
            log.info("[LETISim Engine] 🚀 ЗАПУСК СИМУЛЯЦИИ...");
            for (int i = 1; i <= replications; i++) {
                Thread.sleep(150); // Имитация времени вычислений
                log.info("  -> Прогон {}/{} завершен.", i, replications);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[LETISim Engine] ✅ Симуляция процесса '{}' успешно завершена!", scenario.getName());
        System.out.println("======================================================\n");
    }
}
