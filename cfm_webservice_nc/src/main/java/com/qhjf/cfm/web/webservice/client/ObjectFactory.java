
package com.qhjf.cfm.web.webservice.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.qhjf.cfm.web.webservice.client package. 
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

    private final static QName _PayData_QNAME = new QName("http://com.sinosoft.service.impl/webservice", "payData");
    private final static QName _PayDataResponse_QNAME = new QName("http://com.sinosoft.service.impl/webservice", "payDataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.qhjf.cfm.web.webservice.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PayDataResponse }
     * 
     */
    public PayDataResponse createPayDataResponse() {
        return new PayDataResponse();
    }

    /**
     * Create an instance of {@link PayData }
     * 
     */
    public PayData createPayData() {
        return new PayData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PayData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://com.sinosoft.service.impl/webservice", name = "payData")
    public JAXBElement<PayData> createPayData(PayData value) {
        return new JAXBElement<PayData>(_PayData_QNAME, PayData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PayDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://com.sinosoft.service.impl/webservice", name = "payDataResponse")
    public JAXBElement<PayDataResponse> createPayDataResponse(PayDataResponse value) {
        return new JAXBElement<PayDataResponse>(_PayDataResponse_QNAME, PayDataResponse.class, null, value);
    }

}
