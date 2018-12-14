
package com.seeyon.v3x.services.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.seeyon.v3x.services.impl.Exception;


/**
 * 
 * <pre>
 * &lt;complexType name="ServiceException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://impl.services.v3x.seeyon.com}Exception">
 *       &lt;sequence>
 *         &lt;element name="errorNumber" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceException", propOrder = {
    "errorNumber",
    "message"
})
public class ServiceException
    extends Exception
{

    protected Long errorNumber;
    @XmlElementRef(name = "message", namespace = "http://services.v3x.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> message;

    /**
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getErrorNumber() {
        return errorNumber;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setErrorNumber(Long value) {
        this.errorNumber = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMessage() {
        return message;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMessage(JAXBElement<String> value) {
        this.message = value;
    }

}
