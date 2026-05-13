package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("======================================================");
        log.info("  ДЕМO: Интеграция BPSim-парсера и ядра LETISim BPMS  ");
        log.info("======================================================");

        try {
            BpsimParserFacade facade = new BpsimParserFacade();
            LetisimEngineMock engineMock = new LetisimEngineMock();

            // Список наших подготовленных процессов
            List<String> processFiles = Arrays.asList(
                    "src/main/resources/process1_credit_card.bpsim",
                    "src/main/resources/process2_loan_approval.bpsim",
                    "src/main/resources/process3_employee_onboarding.bpsim"
            );

            for (String filePath : processFiles) {
                File xmlFile = new File(filePath);
                log.info("\n>>> ПАРСИНГ ФАЙЛА: " + xmlFile.getName() + " <<<");

                // Шаг 1: Парсинг (работа библиотеки)
                List<LetisimScenarioDto> scenarios = facade.parse(xmlFile);

                // Шаг 2: Передача в движок (интеграция)
                for (LetisimScenarioDto dto : scenarios) {
                    engineMock.simulateExecution(dto);
                }
            }

            log.info("\n🎉 Демонстрация успешно завершена!");

        } catch (Exception e) {
            log.error("❌ Ошибка выполнения: " + e.getMessage(), e);
        }
    }
}