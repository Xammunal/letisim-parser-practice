package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import com.letisim.mapper.BpsimMapperImpl;
import org.bpsim.model.BPSimData;
import org.bpsim.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Главный публичный фасад библиотеки парсера BPSim.
 *
 * <p>Оркестрирует полный пайплайн обработки BPSim XML:</p>
 * <ol>
 *   <li>Чтение файла</li>
 *   <li>XSD-валидация</li>
 *   <li>JAXB-десериализация</li>
 *   <li>Маппинг в DTO {@link LetisimScenarioDto}</li>
 * </ol>
 *
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * BpsimParserFacade facade = new BpsimParserFacade();
 * List<LetisimScenarioDto> scenarios = facade.parse(new File("model.bpsim"));
 * scenarios.forEach(System.out::println);
 * }</pre>
 */
public class BpsimParserFacade {

    private static final Logger log = LoggerFactory.getLogger(BpsimParserFacade.class);

    private final BpsimParser parser;
    private final BpsimMapper mapper;

    // ──────────────────────────────────────────────────────────────────────
    //  Конструкторы
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Создаёт фасад с реализациями по умолчанию:
     * {@link BpsimParserService} и {@link BpsimMapperImpl}.
     */
    public BpsimParserFacade() {
        this(new BpsimParserService(), new BpsimMapperImpl());
    }

    /**
     * Создаёт фасад с пользовательскими реализациями парсера и маппера.
     * Полезно для подмены в тестах или расширения логики.
     *
     * @param parser реализация {@link BpsimParser}
     * @param mapper реализация {@link BpsimMapper}
     */
    public BpsimParserFacade(BpsimParser parser, BpsimMapper mapper) {
        this.parser = parser;
        this.mapper = mapper;
        log.info("BpsimParserFacade создан");
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Публичный API
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Парсит BPSim XML-файл и возвращает список сценариев в формате LETISim DTO.
     *
     * @param file XML-файл BPSim
     * @return список {@link LetisimScenarioDto} (пустой список, если сценариев нет)
     * @throws IllegalArgumentException если файл null, не существует или не читается
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    public List<LetisimScenarioDto> parse(File file) {
        validateFile(file);
        log.info("Начинаем обработку файла: {}", file.getAbsolutePath());

        try (InputStream is = new FileInputStream(file)) {
            return parseStream(is);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Не удалось прочитать файл: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Парсит BPSim XML-файл по указанному пути.
     *
     * @param filePath путь к XML-файлу BPSim
     * @return список {@link LetisimScenarioDto}
     * @throws IllegalArgumentException если путь null/пустой, файл не существует
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    public List<LetisimScenarioDto> parse(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Путь к файлу не может быть null или пустым");
        }
        return parse(new File(filePath));
    }

    /**
     * Парсит BPSim XML из произвольного входного потока.
     *
     * <p>Вызывающая сторона отвечает за закрытие потока.</p>
     *
     * @param xmlStream входной поток с XML-документом BPSim
     * @return список {@link LetisimScenarioDto}
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    public List<LetisimScenarioDto> parseStream(InputStream xmlStream) {
        // 1. Валидация XSD + JAXB-десериализация
        BPSimData bpsimData = parser.parseBpsim(xmlStream);

        // 2. Маппинг каждого сценария в DTO
        List<Scenario> scenarios = bpsimData.getScenario();
        if (scenarios == null || scenarios.isEmpty()) {
            log.warn("BPSim-документ не содержит сценариев");
            return Collections.emptyList();
        }

        List<LetisimScenarioDto> result = scenarios.stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());

        log.info("Обработка завершена. Сценариев: {}", result.size());
        return result;
    }

    /**
     * Возвращает «сырой» результат JAXB-парсинга без маппинга в DTO.
     * Полезно, если нужен полный доступ ко всем полям BPSim.
     *
     * @param file XML-файл BPSim
     * @return {@link BPSimData} — корневой JAXB-объект
     * @throws IllegalArgumentException если файл не существует
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    public BPSimData parseRaw(File file) {
        validateFile(file);

        try (InputStream is = new FileInputStream(file)) {
            return parser.parseBpsim(is);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Не удалось прочитать файл: " + file.getAbsolutePath(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Вспомогательные методы
    // ──────────────────────────────────────────────────────────────────────

    private void validateFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("Файл не найден: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("Нет прав на чтение файла: " + file.getAbsolutePath());
        }
    }
}
