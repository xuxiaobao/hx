package com.miaosu.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtil {

	/**
	 * map对象转换为XML
	 *
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public static String mapToXml(Map<String, Object> map) {
		Document document = DocumentHelper.createDocument();
		/**
		 * 将map转为xml字符串
		 */
		createXml(document, map);
		Writer writer = new StringWriter();
		OutputFormat format = new OutputFormat();
		format.setSuppressDeclaration(true);
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
	private static void createXml(Document document, Map<String, Object> map) {
		if (map==null) {
			return ;
		}

		Set<Map.Entry<String, Object>> entries = map.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Element element = document.addElement(key);
			Object v = entry.getValue();
			if (v instanceof List) {
				List list = (List) entry.getValue();
				for (Object value : list) {
					Map<String, Object> map2 = (Map<String, Object>) value;
					createXml(element, map2);
				}
			}else if (v instanceof String){
				element.setText((String) v);
			}
		}
	}
	private static void createXml(Element document, Map<String, Object> map) {
		if (map==null) {
			return ;
		}
		Set<Map.Entry<String, Object>> entries = map.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Element element = document.addElement(key);
			Object v = entry.getValue();
			if (v instanceof List) {
				List list = (List) entry.getValue();
				for (Object value : list) {
					Map<String, Object> map2 = (Map<String, Object>) value;
					createXml(element, map2);
				}
			}else if (v instanceof String){
				element.setText((String) v);
			}
		}

		return ;
	}


	/**
	 * Xml转换为map
	 *
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, Object> xmlToMap(String xml) {
		Map<String, Object> map = new HashMap<String, Object>();
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			map = listNodes(root);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}
	private static Map<String, Object> listNodes(Element root) {
		Iterator<Element> it = root.elementIterator();
		Map<String, Object> map = new HashMap<String, Object>();
		String key = root.getName();
		if (!(it.hasNext())) {
			String value = root.getTextTrim();
			map.put(key, value);
			return map;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (it.hasNext()) {
			Element next = it.next();
			Map<String, Object> cValue = listNodes(next);
			list.add(cValue);
		}
		map.put(key, list);
		return map;
	}

	public static void main(String[] args) throws Exception {
		/**
		 * xml转map
		 */
		String xml = "<Response>\n" +
				"<Datetime>2017-05-16T22:00:17.563+08:00</Datetime>\n" +
				"<ChargeData>\n" +
				"<SerialNum>20170516220015</SerialNum>\n" +
				"<SystemNum>6270246316476604416</SystemNum>\n" +
				"</ChargeData>\n" +
				"</Response>";

		xml = replaceBlank(xml);
		System.out.println(xml);
		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement();
		String systemNum = element.element("SystemNum").asXML();
		System.out.println(systemNum);
	}
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}
