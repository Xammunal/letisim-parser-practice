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
        dto.setName("Fast Track Strategy");
        dto.setReplication(100);
        dto.setElementParameters(new ArrayList<>());

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
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

        NodeList values = node.getElementsByTagName("bpsim:Value");
        if (values.getLength() > 0) {
            return Double.parseDouble(values.item(0).getTextContent());
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element child = (Element) children.item(i);
                if (child.hasAttribute("mean")) {
                    return Double.parseDouble(child.getAttribute("mean"));
                }
                if (child.hasAttribute("scale") && child.hasAttribute("shape")) {
                    return Double.parseDouble(child.getAttribute("scale")) * Double.parseDouble(child.getAttribute("shape"));
                }
            }
        }
        return 0.0;
    }
}