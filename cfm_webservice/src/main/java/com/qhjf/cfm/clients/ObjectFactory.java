
package com.qhjf.cfm.clients;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.qhjf.cfm.clients package. 
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

    private final static QName _PushStatusToken_QNAME = new QName("http://metlife.services.v3x.seeyon.com", "token");
    private final static QName _PushStatusXmlString_QNAME = new QName("http://metlife.services.v3x.seeyon.com", "xmlString");
    private final static QName _PushStatusResponseReturn_QNAME = new QName("http://metlife.services.v3x.seeyon.com", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.qhjf.cfm.clients
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PushStatus }
     * 
     */
    public PushStatus createPushStatus() {
        return new PushStatus();
    }

    /**
     * Create an instance of {@link PushStatusResponse }
     * 
     */
    public PushStatusResponse createPushStatusResponse() {
        return new PushStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://metlife.services.v3x.seeyon.com", name = "token", scope = PushStatus.class)
    public JAXBElement<String> createPushStatusToken(String value) {
        return new JAXBElement<String>(_PushStatusToken_QNAME, String.class, PushStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://metlife.services.v3x.seeyon.com", name = "xmlString", scope = PushStatus.class)
    public JAXBElement<String> createPushStatusXmlString(String value) {
        return new JAXBElement<String>(_PushStatusXmlString_QNAME, String.class, PushStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://metlife.services.v3x.seeyon.com", name = "return", scope = PushStatusResponse.class)
    public JAXBElement<String> createPushStatusResponseReturn(String value) {
        return new JAXBElement<String>(_PushStatusResponseReturn_QNAME, String.class, PushStatusResponse.class, value);
    }

}
