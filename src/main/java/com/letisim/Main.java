package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  ДЕМO: Интеграция BPSim-парсера и ядра LETISim BPMS  ");
        System.out.println("======================================================");

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
                System.out.println("\n>>> ПАРСИНГ ФАЙЛА: " + xmlFile.getName() + " <<<");

                // Шаг 1: Парсинг (работа библиотеки)
                List<LetisimScenarioDto> scenarios = facade.parse(xmlFile);

                // Шаг 2: Передача в движок (интеграция)
                for (LetisimScenarioDto dto : scenarios) {
                    engineMock.simulateExecution(dto);
                }
            }

            System.out.println("\n🎉 Демонстрация успешно завершена!");

        } catch (Exception e) {
            System.err.println("❌ Ошибка выполнения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}