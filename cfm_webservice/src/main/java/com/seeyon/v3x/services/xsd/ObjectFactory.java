
package com.seeyon.v3x.services.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeyon.v3x.services.xsd package. 
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

    private final static QName _ServiceExceptionMessage_QNAME = new QName("http://services.v3x.seeyon.com/xsd", "message");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeyon.v3x.services.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceException }
     * 
     */
    public ServiceException createServiceException() {
        return new ServiceException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.v3x.seeyon.com/xsd", name = "message", scope = ServiceException.class)
    public JAXBElement<String> createServiceExceptionMessage(String value) {
        return new JAXBElement<String>(_ServiceExceptionMessage_QNAME, String.class, ServiceException.class, value);
    }

}
