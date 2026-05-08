package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Инициализация библиотеки...");
            BpsimParserFacade facade = new BpsimParserFacade();

            // Указываем путь к твоему тестовому файлу из ресурсов
            File xmlFile = new File("src/main/resources/sample.xml");

            System.out.println("Парсинг файла: " + xmlFile.getAbsolutePath());
            List<LetisimScenarioDto> scenarios = facade.parse(xmlFile);

            System.out.println("✅ Успешно! Найдено сценариев: " + scenarios.size());

            for (LetisimScenarioDto dto : scenarios) {
                System.out.println("ID: " + dto.getId());
                System.out.println("Имя: " + dto.getName());
                System.out.println("Ед. времени: " + dto.getBaseTimeUnit());
                System.out.println("Кол-во элементов: " + dto.getElementParameterCount());
                System.out.println("---");
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка выполнения: " + e.getMessage());
        }
    }
}