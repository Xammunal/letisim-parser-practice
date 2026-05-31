package com.letisim;

import com.letisim.dto.ElementParameterDto;
import com.letisim.dto.LetisimScenarioDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class BpsimXmlAdapter {

    public LetisimScenarioDto parse(String filePath) {
        LetisimScenarioDto dto = new LetisimScenarioDto();
        dto.setElementParameters(new ArrayList<>());

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));

            // 1. Динамически читаем имя сценария
            NodeList scenarios = doc.getElementsByTagName("bpsim:Scenario");
            if (scenarios.getLength() > 0) {
                dto.setName(((Element) scenarios.item(0)).getAttribute("name"));
            } else {
                dto.setName("Imported Vendor Scenario");
            }

            // 2. Ловим "мутантский" атрибут processInstances вместо стандартного replication
            NodeList params = doc.getElementsByTagName("bpsim:ScenarioParameters");
            if (params.getLength() > 0) {
                Element paramEl = (Element) params.item(0);
                if (paramEl.hasAttribute("processInstances")) {
                    dto.setReplication(Integer.parseInt(paramEl.getAttribute("processInstances")));
                } else {
                    dto.setReplication(1);
                }
            }

            // 3. Вытаскиваем кривые нестандартные теги ElementParameter (в единственном числе)
            NodeList elements = doc.getElementsByTagName("bpsim:ElementParameter");

            for (int i = 0; i < elements.getLength(); i++) {
                Element el = (Element) elements.item(i);
                ElementParameterDto param = new ElementParameterDto();

                param.setElementRef(el.getAttribute("elementRef"));
                param.setDistributionMean(extractValueOrMean(el, "bpsim:Duration"));

                Double cost = extractValueOrMean(el, "bpsim:FixedCost");
                if (cost != null) param.setUnitCost(cost);

                Double prob = extractValueOrMean(el, "bpsim:Probability");
                if (prob != null) param.setProbability(prob);

                dto.getElementParameters().add(param);
            }
        } catch (Exception e) {
            System.err.println("Ошибка XML-адаптера: " + e.getMessage());
        }
        return dto;
    }

    private Double extractValueOrMean(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return null;
        Element node = (Element) nodes.item(0);

        // Поиск фиксированного значения (например, FixedCost или Probability)
        NodeList values = node.getElementsByTagName("bpsim:Value");
        if (values.getLength() > 0) {
            return Double.parseDouble(values.item(0).getTextContent());
        }

        // Пробиваем глубокие обертки генератора, чтобы найти распределения
        String[] distTypes = {"LogNormalDistribution", "GammaDistribution", "ErlangDistribution", "PoissonDistribution", "BetaDistribution"};
        for (String distType : distTypes) {
            NodeList distNodes = node.getElementsByTagName("bpsim:" + distType);
            if (distNodes.getLength() > 0) {
                Element distNode = (Element) distNodes.item(0);
                if (distNode.hasAttribute("mean")) {
                    return Double.parseDouble(distNode.getAttribute("mean"));
                }
                // Для гаммы или беты берем scale/alpha как базовое значение для симуляции
                if (distNode.hasAttribute("scale")) {
                    return Double.parseDouble(distNode.getAttribute("scale"));
                }
                if (distNode.hasAttribute("alpha")) {
                    return Double.parseDouble(distNode.getAttribute("alpha"));
                }
                return 5.0; // Fallback если параметры вообще другие
            }
        }
        return 0.0;
    }
}