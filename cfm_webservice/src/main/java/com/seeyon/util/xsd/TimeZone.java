
package com.seeyon.util.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * <pre>
 * &lt;complexType name="TimeZone">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availableIDs" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dSTSavings" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="default" type="{http://util.java/xsd}TimeZone" minOccurs="0"/>
 *         &lt;element name="defaultInAppContext" type="{http://util.java/xsd}TimeZone" minOccurs="0"/>
 *         &lt;element name="defaultRef" type="{http://util.java/xsd}TimeZone" minOccurs="0"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rawOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="systemGMTOffsetID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeZone", propOrder = {
    "availableIDs",
    "dstSavings",
    "_default",
    "defaultInAppContext",
    "defaultRef",
    "displayName",
    "id",
    "rawOffset",
    "systemGMTOffsetID"
})
public class TimeZone {

    @XmlElement(nillable = true)
    protected List<String> availableIDs;
    @XmlElement(name = "dSTSavings")
    protected Integer dstSavings;
    @XmlElementRef(name = "default", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeZone> _default;
    @XmlElementRef(name = "defaultInAppContext", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeZone> defaultInAppContext;
    @XmlElementRef(name = "defaultRef", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeZone> defaultRef;
    @XmlElementRef(name = "displayName", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> displayName;
    @XmlElementRef(name = "iD", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> id;
    protected Integer rawOffset;
    @XmlElementRef(name = "systemGMTOffsetID", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> systemGMTOffsetID;

    /**
     * Gets the value of the availableIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the availableIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvailableIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAvailableIDs() {
        if (availableIDs == null) {
            availableIDs = new ArrayList<String>();
        }
        return this.availableIDs;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDSTSavings() {
        return dstSavings;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDSTSavings(Integer value) {
        this.dstSavings = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public JAXBElement<TimeZone> getDefault() {
        return _default;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public void setDefault(JAXBElement<TimeZone> value) {
        this._default = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public JAXBElement<TimeZone> getDefaultInAppContext() {
        return defaultInAppContext;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public void setDefaultInAppContext(JAXBElement<TimeZone> value) {
        this.defaultInAppContext = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public JAXBElement<TimeZone> getDefaultRef() {
        return defaultRef;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public void setDefaultRef(JAXBElement<TimeZone> value) {
        this.defaultRef = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDisplayName() {
        return displayName;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayName(JAXBElement<String> value) {
        this.displayName = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getID() {
        return id;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setID(JAXBElement<String> value) {
        this.id = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRawOffset() {
        return rawOffset;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRawOffset(Integer value) {
        this.rawOffset = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSystemGMTOffsetID() {
        return systemGMTOffsetID;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSystemGMTOffsetID(JAXBElement<String> value) {
        this.systemGMTOffsetID = value;
    }

}
