package com.hu.iJogging.common;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OfflineMapCitiesParser {
  public Set<OfflineCityItem> offlineCities = null;

  public Set<OfflineCityItem> parse(InputStream is) throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance(); // ȡ��SAXParserFactoryʵ��
    SAXParser parser = factory.newSAXParser(); // ��factory��ȡSAXParserʵ��
    OfflineMapCitiesHandler handler = new OfflineMapCitiesHandler(); // ʵ�����Զ���Handler
    parser.parse(is, handler); // �����Զ���Handler�������������
    return offlineCities;
  }

  private class OfflineMapCitiesHandler extends DefaultHandler {

    private StringBuilder builder;
    private OfflineCityItem offlineCity;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      super.characters(ch, start, length);
      builder.append(ch, start, length); // ����ȡ���ַ�����׷�ӵ�builder��
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      super.endElement(uri, localName, qName);
      if (localName.equals("name")) {
        offlineCity.name = builder.toString();
      } else if (localName.equals("province")) {
        offlineCity.province = builder.toString();
      } else if (localName.equals("ArLowUrl")) {
        offlineCity.ArLowUrl = builder.toString();
      } else if (localName.equals("ArLowSize")) {
        offlineCity.ArLowSize = builder.toString();
      } else if (localName.equals("ArHighUrl")) {
        offlineCity.ArHighUrl = builder.toString();
      } else if (localName.equals("ArHighSize")) {
        offlineCity.ArHighSize = builder.toString();
      } else if (localName.equals("city")) {
        offlineCities.add(offlineCity);
      }
    }

    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
      offlineCities = new HashSet<OfflineCityItem>();
      builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if (localName.equals("city")) {
        offlineCity = new OfflineCityItem();
      }
      builder.setLength(0); // ���ַ���������Ϊ0 �Ա����¿�ʼ��ȡԪ���ڵ��ַ��ڵ�
    }

  }
}
