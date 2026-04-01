import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск парсера BPSim...");

        try {
            // 1. Указываем JAXB, где лежат наши сгенерированные классы
            JAXBContext context = JAXBContext.newInstance("org.bpsim.model");

            // 2. Создаем Unmarshaller (сам парсер)
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // 3. Указываем путь к XML-файлу, который будем парсить
            File xmlFile = new File("src/main/resources/sample.xml");

            // Проверяем, существует ли файл
            if (!xmlFile.exists()) {
                System.out.println("Ошибка: Файл sample.xml не найден!");
                return;
            }

            // 4. Парсим XML в Java-объект
            Object parsedObject = unmarshaller.unmarshal(xmlFile);

            System.out.println("✅ Парсинг прошел успешно!");
            System.out.println("Тип корневого объекта: " + parsedObject.getClass().getName());

            // Дальше мы сможем вытаскивать данные из объекта, например:
            // BPSimData data = (BPSimData) parsedObject;
            // System.out.println("Сценариев внутри: " + data.getScenario().size());

        } catch (JAXBException e) {
            System.out.println("❌ Ошибка при парсинге XML:");
            e.printStackTrace();
        }
    }
}