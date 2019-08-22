
package com.seeyon.util.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the java.util.xsd package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TimeZoneDefault_QNAME = new QName("http://util.java/xsd", "default");
    private final static QName _TimeZoneDefaultRef_QNAME = new QName("http://util.java/xsd", "defaultRef");
    private final static QName _TimeZoneDisplayName_QNAME = new QName("http://util.java/xsd", "displayName");
    private final static QName _TimeZoneSystemGMTOffsetID_QNAME = new QName("http://util.java/xsd", "systemGMTOffsetID");
    private final static QName _TimeZoneDefaultInAppContext_QNAME = new QName("http://util.java/xsd", "defaultInAppContext");
    private final static QName _TimeZoneID_QNAME = new QName("http://util.java/xsd", "iD");
    private final static QName _LocaleCountry_QNAME = new QName("http://util.java/xsd", "country");
    private final static QName _LocaleDisplayCountry_QNAME = new QName("http://util.java/xsd", "displayCountry");
    private final static QName _LocaleDisplayVariant_QNAME = new QName("http://util.java/xsd", "displayVariant");
    private final static QName _LocaleLanguage_QNAME = new QName("http://util.java/xsd", "language");
    private final static QName _LocaleVariant_QNAME = new QName("http://util.java/xsd", "variant");
    private final static QName _LocaleISO3Country_QNAME = new QName("http://util.java/xsd", "iSO3Country");
    private final static QName _LocaleDisplayLanguage_QNAME = new QName("http://util.java/xsd", "displayLanguage");
    private final static QName _LocaleISO3Language_QNAME = new QName("http://util.java/xsd", "iSO3Language");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: java.util.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Locale }
     * 
     */
    public Locale createLocale() {
        return new Locale();
    }

    /**
     * Create an instance of {@link TimeZone }
     * 
     */
    public TimeZone createTimeZone() {
        return new TimeZone();
    }

    /**
     * Create an instance of {@link Set }
     * 
     */
    public Set createSet() {
        return new Set();
    }

    /**
     * Create an instance of {@link Map }
     * 
     */
    public Map createMap() {
        return new Map();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeZone }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "default", scope = TimeZone.class)
    public JAXBElement<TimeZone> createTimeZoneDefault(TimeZone value) {
        return new JAXBElement<TimeZone>(_TimeZoneDefault_QNAME, TimeZone.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeZone }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "defaultRef", scope = TimeZone.class)
    public JAXBElement<TimeZone> createTimeZoneDefaultRef(TimeZone value) {
        return new JAXBElement<TimeZone>(_TimeZoneDefaultRef_QNAME, TimeZone.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "displayName", scope = TimeZone.class)
    public JAXBElement<String> createTimeZoneDisplayName(String value) {
        return new JAXBElement<String>(_TimeZoneDisplayName_QNAME, String.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "systemGMTOffsetID", scope = TimeZone.class)
    public JAXBElement<String> createTimeZoneSystemGMTOffsetID(String value) {
        return new JAXBElement<String>(_TimeZoneSystemGMTOffsetID_QNAME, String.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeZone }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "defaultInAppContext", scope = TimeZone.class)
    public JAXBElement<TimeZone> createTimeZoneDefaultInAppContext(TimeZone value) {
        return new JAXBElement<TimeZone>(_TimeZoneDefaultInAppContext_QNAME, TimeZone.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "iD", scope = TimeZone.class)
    public JAXBElement<String> createTimeZoneID(String value) {
        return new JAXBElement<String>(_TimeZoneID_QNAME, String.class, TimeZone.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Locale }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "default", scope = Locale.class)
    public JAXBElement<Locale> createLocaleDefault(Locale value) {
        return new JAXBElement<Locale>(_TimeZoneDefault_QNAME, Locale.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "country", scope = Locale.class)
    public JAXBElement<String> createLocaleCountry(String value) {
        return new JAXBElement<String>(_LocaleCountry_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "displayName", scope = Locale.class)
    public JAXBElement<String> createLocaleDisplayName(String value) {
        return new JAXBElement<String>(_TimeZoneDisplayName_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "displayCountry", scope = Locale.class)
    public JAXBElement<String> createLocaleDisplayCountry(String value) {
        return new JAXBElement<String>(_LocaleDisplayCountry_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "displayVariant", scope = Locale.class)
    public JAXBElement<String> createLocaleDisplayVariant(String value) {
        return new JAXBElement<String>(_LocaleDisplayVariant_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "language", scope = Locale.class)
    public JAXBElement<String> createLocaleLanguage(String value) {
        return new JAXBElement<String>(_LocaleLanguage_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "variant", scope = Locale.class)
    public JAXBElement<String> createLocaleVariant(String value) {
        return new JAXBElement<String>(_LocaleVariant_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "iSO3Country", scope = Locale.class)
    public JAXBElement<String> createLocaleISO3Country(String value) {
        return new JAXBElement<String>(_LocaleISO3Country_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "displayLanguage", scope = Locale.class)
    public JAXBElement<String> createLocaleDisplayLanguage(String value) {
        return new JAXBElement<String>(_LocaleDisplayLanguage_QNAME, String.class, Locale.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://util.java/xsd", name = "iSO3Language", scope = Locale.class)
    public JAXBElement<String> createLocaleISO3Language(String value) {
        return new JAXBElement<String>(_LocaleISO3Language_QNAME, String.class, Locale.class, value);
    }

}
