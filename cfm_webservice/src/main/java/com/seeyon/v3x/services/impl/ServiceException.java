
package com.seeyon.v3x.services.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceException" type="{http://services.v3x.seeyon.com/xsd}ServiceException" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceException"
})
@XmlRootElement(name = "ServiceException")
public class ServiceException {

    @XmlElementRef(name = "ServiceException", namespace = "http://impl.services.v3x.seeyon.com", type = JAXBElement.class, required = false)
    protected JAXBElement<com.seeyon.v3x.services.xsd.ServiceException> serviceException;

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link com.seeyon.v3x.services.xsd.ServiceException }{@code >}
     *     
     */
    public JAXBElement<com.seeyon.v3x.services.xsd.ServiceException> getServiceException() {
        return serviceException;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link com.seeyon.v3x.services.xsd.ServiceException }{@code >}
     *     
     */
    public void setServiceException(JAXBElement<com.seeyon.v3x.services.xsd.ServiceException> value) {
        this.serviceException = value;
    }

}
