package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("  LETISim: Модуль генерации аналитики Heatmap         ");

        LetisimEngineMock engineMock = new LetisimEngineMock();

        // Используем DOM-адаптер специально для кривых файлов сторонних вендоров
        BpsimXmlAdapter vendorAdapter = new BpsimXmlAdapter();

        try {
            log.info("Парсинг файла через Vendor DOM Adapter...");
            String externalPath = "src/main/resources/strategy_10_fast_track.bpmn";
            LetisimScenarioDto scenario = vendorAdapter.parse(externalPath);

            log.info("Успешно извлечен сценарий: '{}', Репликаций: {}", scenario.getName(), scenario.getReplication());

            // Симулируем!
            engineMock.simulateExecution(scenario);

            log.info("\nПайплайн симуляции успешно отработал.");

        } catch (Exception e) {
            log.error("Критическая ошибка выполнения: " + e.getMessage(), e);
        }
    }
}