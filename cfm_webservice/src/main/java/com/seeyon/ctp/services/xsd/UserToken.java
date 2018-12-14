
package com.seeyon.ctp.services.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.seeyon.ctp.common.authenticate.domain.xsd.User;


/**
 * 
 * <pre>
 * &lt;complexType name="UserToken">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bindingUser" type="{http://domain.authenticate.common.ctp.seeyon.com/xsd}User" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nullToken" type="{http://services.ctp.seeyon.com/xsd}UserToken" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToken", propOrder = {
    "bindingUser",
    "id",
    "nullToken"
})
public class UserToken {

    @XmlElementRef(name = "bindingUser", namespace = "http://services.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<User> bindingUser;
    @XmlElementRef(name = "id", namespace = "http://services.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> id;
    @XmlElementRef(name = "nullToken", namespace = "http://services.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<UserToken> nullToken;

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link User }{@code >}
     *     
     */
    public JAXBElement<User> getBindingUser() {
        return bindingUser;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link User }{@code >}
     *     
     */
    public void setBindingUser(JAXBElement<User> value) {
        this.bindingUser = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getId() {
        return id;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setId(JAXBElement<String> value) {
        this.id = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UserToken }{@code >}
     *     
     */
    public JAXBElement<UserToken> getNullToken() {
        return nullToken;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UserToken }{@code >}
     *     
     */
    public void setNullToken(JAXBElement<UserToken> value) {
        this.nullToken = value;
    }

}
