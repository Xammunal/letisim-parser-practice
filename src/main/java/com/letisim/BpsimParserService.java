package com.letisim;

import org.bpsim.model.BPSimData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.net.URL;

/**
 * Сервис для безопасного парсинга и строгой XSD-валидации
 * файлов BPSim 2.0.
 *
 * <p>Основные особенности:</p>
 * <ul>
 *   <li>JAXBContext создаётся один раз в конструкторе —
 *       это тяжёлая thread-safe операция, которую нельзя повторять при каждом вызове.</li>
 *   <li>Schema (XSD) компилируется один раз и переиспользуется.</li>
 *   <li>Unmarshaller создаётся на каждый вызов (он НЕ thread-safe).</li>
 * </ul>
 */
public class BpsimParserService implements BpsimParser {

    private static final Logger log = LoggerFactory.getLogger(BpsimParserService.class);

    /** XSD-файл внутри classpath (src/main/resources/xsd/BPSim.xsd) */
    private static final String XSD_RESOURCE_PATH = "xsd/BPSim.xsd";

    /** Thread-safe: создаётся один раз */
    private final JAXBContext jaxbContext;

    /** Thread-safe: можно переиспользовать между потоками */
    private final Schema bpsimSchema;

    // ──────────────────────────────────────────────────────────────────────
    //  Конструктор (инициализация)
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Создаёт сервис, инициализируя JAXBContext и компилируя XSD-схему.
     *
     * @throws IllegalStateException если не удалось найти XSD или создать контекст.
     */
    public BpsimParserService() {
        log.info("Инициализация BpsimParserService...");

        try {
            // 1. JAXBContext — тяжёлый объект, создаём один раз
            this.jaxbContext = JAXBContext.newInstance(BPSimData.class);
            log.info("JAXBContext создан для {}", BPSimData.class.getName());
        } catch (JAXBException e) {
            throw new IllegalStateException(
                    "Не удалось создать JAXBContext для org.bpsim.model", e);
        }

        try {
            // 2. Загружаем XSD из classpath
            URL xsdUrl = getClass().getClassLoader().getResource(XSD_RESOURCE_PATH);
            if (xsdUrl == null) {
                throw new IllegalStateException(
                        "XSD-схема не найдена в classpath: " + XSD_RESOURCE_PATH);
            }

            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.bpsimSchema = schemaFactory.newSchema(xsdUrl);
            log.info("XSD-схема BPSim скомпилирована: {}", xsdUrl);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось скомпилировать XSD-схему BPSim", e);
        }

        log.info("BpsimParserService инициализирован успешно ✅");
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Публичный API
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Парсит входящий XML-поток BPSim с предварительной
     * строгой XSD-валидацией.
     *
     * <p>Если XML не соответствует схеме BPSim.xsd — выбрасывается
     * {@link BpsimValidationException}.</p>
     *
     * @param xmlStream входной поток с XML-документом BPSim
     * @return десериализованный {@link BPSimData}
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    @Override
    public BPSimData parseBpsim(InputStream xmlStream) {
        log.info("Начинаем парсинг BPSim XML...");

        try {
            // Unmarshaller НЕ thread-safe — создаём новый на каждый вызов
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Подключаем строгую XSD-валидацию
            unmarshaller.setSchema(bpsimSchema);

            // Парсим с валидацией
            Object result = unmarshaller.unmarshal(xmlStream);

            if (!(result instanceof BPSimData)) {
                throw new BpsimValidationException(
                        "Корневой элемент XML не является BPSimData. "
                                + "Получен: " + result.getClass().getName());
            }

            BPSimData data = (BPSimData) result;
            log.info("Парсинг завершён успешно ✅ Сценариев: {}",
                    data.getScenario().size());

            return data;

        } catch (JAXBException e) {
            // JAXBException при включённой Schema => ошибка валидации XSD
            String msg = "XML не прошёл XSD-валидацию BPSim";
            if (e.getLinkedException() != null) {
                msg += ": " + e.getLinkedException().getMessage();
            }
            log.error("Ошибка валидации ❌ {}", msg);
            throw new BpsimValidationException(msg, e);
        }
    }
}
