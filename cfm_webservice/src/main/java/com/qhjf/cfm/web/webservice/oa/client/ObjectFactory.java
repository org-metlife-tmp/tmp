
package com.qhjf.cfm.web.webservice.oa.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.qhjf.cfm.web.webservice.oa.server package. 
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

    private final static QName _ReciveDateResponse_QNAME = new QName("http://server.oa.webservice.web.cfm.qhjf.com/", "reciveDateResponse");
    private final static QName _QueryStatus_QNAME = new QName("http://server.oa.webservice.web.cfm.qhjf.com/", "queryStatus");
    private final static QName _ReciveDate_QNAME = new QName("http://server.oa.webservice.web.cfm.qhjf.com/", "reciveDate");
    private final static QName _QueryStatusResponse_QNAME = new QName("http://server.oa.webservice.web.cfm.qhjf.com/", "queryStatusResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.qhjf.cfm.web.webservice.oa.server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link QueryStatusResponse }
     * 
     */
    public QueryStatusResponse createQueryStatusResponse() {
        return new QueryStatusResponse();
    }

    /**
     * Create an instance of {@link QueryStatus }
     * 
     */
    public QueryStatus createQueryStatus() {
        return new QueryStatus();
    }

    /**
     * Create an instance of {@link ReciveDate }
     * 
     */
    public ReciveDate createReciveDate() {
        return new ReciveDate();
    }

    /**
     * Create an instance of {@link ReciveDateResponse }
     * 
     */
    public ReciveDateResponse createReciveDateResponse() {
        return new ReciveDateResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReciveDateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.oa.webservice.web.cfm.qhjf.com/", name = "reciveDateResponse")
    public JAXBElement<ReciveDateResponse> createReciveDateResponse(ReciveDateResponse value) {
        return new JAXBElement<ReciveDateResponse>(_ReciveDateResponse_QNAME, ReciveDateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.oa.webservice.web.cfm.qhjf.com/", name = "queryStatus")
    public JAXBElement<QueryStatus> createQueryStatus(QueryStatus value) {
        return new JAXBElement<QueryStatus>(_QueryStatus_QNAME, QueryStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReciveDate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.oa.webservice.web.cfm.qhjf.com/", name = "reciveDate")
    public JAXBElement<ReciveDate> createReciveDate(ReciveDate value) {
        return new JAXBElement<ReciveDate>(_ReciveDate_QNAME, ReciveDate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.oa.webservice.web.cfm.qhjf.com/", name = "queryStatusResponse")
    public JAXBElement<QueryStatusResponse> createQueryStatusResponse(QueryStatusResponse value) {
        return new JAXBElement<QueryStatusResponse>(_QueryStatusResponse_QNAME, QueryStatusResponse.class, null, value);
    }

}
