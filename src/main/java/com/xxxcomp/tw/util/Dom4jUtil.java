package com.xxxcomp.tw.util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.*;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/3, MarkHuang,new
 * </ul>
 * @since 2018/2/3
 */
public class Dom4jUtil {
    /**
     * Avoid  DocumentHelper createElement method  auto create xml name space</br>
     * set name space same to parent element
     *
     * @param elementName element name who you want to create
     * @param rootElement root element from this xml file
     * @param text        element text
     * @return new Element
     */
    public static Element createElement(String elementName, Element rootElement, String text) {
        Element element = DocumentHelper.createElement(new QName(elementName
                , rootElement.getNamespace()));
        if (text != null) element.addText(text);
        return element;
    }

    public static Element createElement(String elementName, Element rootElement) {
        return createElement(elementName, rootElement, null);
    }

    /**
     * Analysis xml file
     *
     * @return Document
     */
    public static Document analysisXml(File xmlFile) {
        String[] fileNames = xmlFile.getName().split("\\.");
        if (!(fileNames.length == 2 && "xml".equals(fileNames[1]))) return null;
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new FileInputStream(xmlFile));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static void writeXml(Document doc,File file) {
        writeXml(doc,file,true);
    }

    /**
     * Write xml file
     * @param doc xml document
     * @param file target file
     * @param escape escape special character
     */
    public static void writeXml(Document doc,File file,boolean escape) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(file), format);
            writer.setEscapeText(escape);
            writer.write(doc);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
