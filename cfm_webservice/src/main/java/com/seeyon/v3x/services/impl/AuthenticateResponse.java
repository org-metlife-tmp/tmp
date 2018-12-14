
package com.seeyon.v3x.services.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.seeyon.ctp.services.xsd.UserToken;


/**
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://services.ctp.seeyon.com/xsd}UserToken" minOccurs="0"/>
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
    "_return"
})
@XmlRootElement(name = "authenticateResponse")
public class AuthenticateResponse {

    @XmlElementRef(name = "return", namespace = "http://impl.services.v3x.seeyon.com", type = JAXBElement.class, required = false)
    protected JAXBElement<UserToken> _return;

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UserToken }{@code >}
     *     
     */
    public JAXBElement<UserToken> getReturn() {
        return _return;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UserToken }{@code >}
     *     
     */
    public void setReturn(JAXBElement<UserToken> value) {
        this._return = value;
    }

}
