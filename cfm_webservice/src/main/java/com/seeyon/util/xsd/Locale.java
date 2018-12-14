
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
 * &lt;complexType name="Locale">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availableLocales" type="{http://util.java/xsd}Locale" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default" type="{http://util.java/xsd}Locale" minOccurs="0"/>
 *         &lt;element name="displayCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayLanguage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayVariant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iSO3Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iSO3Language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iSOCountries" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="iSOLanguages" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="variant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Locale", propOrder = {
    "availableLocales",
    "country",
    "_default",
    "displayCountry",
    "displayLanguage",
    "displayName",
    "displayVariant",
    "iso3Country",
    "iso3Language",
    "isoCountries",
    "isoLanguages",
    "language",
    "variant"
})
public class Locale {

    @XmlElement(nillable = true)
    protected List<Locale> availableLocales;
    @XmlElementRef(name = "country", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> country;
    @XmlElementRef(name = "default", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Locale> _default;
    @XmlElementRef(name = "displayCountry", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> displayCountry;
    @XmlElementRef(name = "displayLanguage", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> displayLanguage;
    @XmlElementRef(name = "displayName", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> displayName;
    @XmlElementRef(name = "displayVariant", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> displayVariant;
    @XmlElementRef(name = "iSO3Country", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> iso3Country;
    @XmlElementRef(name = "iSO3Language", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> iso3Language;
    @XmlElement(name = "iSOCountries", nillable = true)
    protected List<String> isoCountries;
    @XmlElement(name = "iSOLanguages", nillable = true)
    protected List<String> isoLanguages;
    @XmlElementRef(name = "language", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> language;
    @XmlElementRef(name = "variant", namespace = "http://util.java/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> variant;

    /**
     * Gets the value of the availableLocales property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the availableLocales property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvailableLocales().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Locale }
     * 
     * 
     */
    public List<Locale> getAvailableLocales() {
        if (availableLocales == null) {
            availableLocales = new ArrayList<Locale>();
        }
        return this.availableLocales;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCountry() {
        return country;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCountry(JAXBElement<String> value) {
        this.country = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Locale }{@code >}
     *     
     */
    public JAXBElement<Locale> getDefault() {
        return _default;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Locale }{@code >}
     *     
     */
    public void setDefault(JAXBElement<Locale> value) {
        this._default = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDisplayCountry() {
        return displayCountry;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayCountry(JAXBElement<String> value) {
        this.displayCountry = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDisplayLanguage() {
        return displayLanguage;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayLanguage(JAXBElement<String> value) {
        this.displayLanguage = value;
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
    public JAXBElement<String> getDisplayVariant() {
        return displayVariant;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayVariant(JAXBElement<String> value) {
        this.displayVariant = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getISO3Country() {
        return iso3Country;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setISO3Country(JAXBElement<String> value) {
        this.iso3Country = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getISO3Language() {
        return iso3Language;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setISO3Language(JAXBElement<String> value) {
        this.iso3Language = value;
    }

    /**
     * Gets the value of the isoCountries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the isoCountries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getISOCountries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getISOCountries() {
        if (isoCountries == null) {
            isoCountries = new ArrayList<String>();
        }
        return this.isoCountries;
    }

    /**
     * Gets the value of the isoLanguages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the isoLanguages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getISOLanguages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getISOLanguages() {
        if (isoLanguages == null) {
            isoLanguages = new ArrayList<String>();
        }
        return this.isoLanguages;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLanguage() {
        return language;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLanguage(JAXBElement<String> value) {
        this.language = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVariant() {
        return variant;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVariant(JAXBElement<String> value) {
        this.variant = value;
    }

}
