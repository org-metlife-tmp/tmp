
package com.seeyon.ctp.common.authenticate.domain.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.seeyon.util.xsd.Locale;
import com.seeyon.util.xsd.Map;
import com.seeyon.util.xsd.Set;
import com.seeyon.util.xsd.TimeZone;


/**
 * 
 * <pre>
 * &lt;complexType name="User">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accessSystemMenu" type="{http://util.java/xsd}Set" minOccurs="0"/>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="admin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="administrator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="auditAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="canSendSMS" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="customizes" type="{http://util.java/xsd}Map" minOccurs="0"/>
 *         &lt;element name="departmentId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="etagRandom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="fontSize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fromM1" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="groupAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="internal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="levelId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="locale" type="{http://util.java/xsd}Locale" minOccurs="0"/>
 *         &lt;element name="loginAccount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="loginAccountName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginAccountShortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginLogId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="loginName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginSign" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="loginTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="mainFrame" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="platformAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="postId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="remoteAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceJsonStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="securityKey" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="skin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="superAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="systemAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="timeZone" type="{http://util.java/xsd}TimeZone" minOccurs="0"/>
 *         &lt;element name="userAgentFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userInfoJsonStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userSSOFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="v5Member" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {
    "accessSystemMenu",
    "accountId",
    "admin",
    "administrator",
    "auditAdmin",
    "canSendSMS",
    "customizes",
    "departmentId",
    "etagRandom",
    "externalType",
    "fontSize",
    "fromM1",
    "groupAdmin",
    "id",
    "internal",
    "levelId",
    "locale",
    "loginAccount",
    "loginAccountName",
    "loginAccountShortName",
    "loginLogId",
    "loginName",
    "loginSign",
    "loginTimestamp",
    "mainFrame",
    "name",
    "password",
    "platformAdmin",
    "postId",
    "remoteAddr",
    "resourceJsonStr",
    "securityKey",
    "sessionId",
    "skin",
    "superAdmin",
    "systemAdmin",
    "timeZone",
    "userAgentFrom",
    "userInfoJsonStr",
    "userSSOFrom",
    "v5Member"
})
public class User {

    @XmlElementRef(name = "accessSystemMenu", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Set> accessSystemMenu;
    @XmlElementRef(name = "accountId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> accountId;
    protected Boolean admin;
    protected Boolean administrator;
    protected Boolean auditAdmin;
    protected Boolean canSendSMS;
    @XmlElementRef(name = "customizes", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Map> customizes;
    @XmlElementRef(name = "departmentId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> departmentId;
    @XmlElementRef(name = "etagRandom", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> etagRandom;
    @XmlElementRef(name = "externalType", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> externalType;
    @XmlElementRef(name = "fontSize", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> fontSize;
    protected Boolean fromM1;
    protected Boolean groupAdmin;
    @XmlElementRef(name = "id", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> id;
    protected Boolean internal;
    @XmlElementRef(name = "levelId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> levelId;
    @XmlElementRef(name = "locale", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Locale> locale;
    @XmlElementRef(name = "loginAccount", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> loginAccount;
    @XmlElementRef(name = "loginAccountName", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> loginAccountName;
    @XmlElementRef(name = "loginAccountShortName", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> loginAccountShortName;
    @XmlElementRef(name = "loginLogId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> loginLogId;
    @XmlElementRef(name = "loginName", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> loginName;
    protected Integer loginSign;
    @XmlElementRef(name = "loginTimestamp", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> loginTimestamp;
    @XmlElementRef(name = "mainFrame", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mainFrame;
    @XmlElementRef(name = "name", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElementRef(name = "password", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> password;
    protected Boolean platformAdmin;
    @XmlElementRef(name = "postId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> postId;
    @XmlElementRef(name = "remoteAddr", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> remoteAddr;
    @XmlElementRef(name = "resourceJsonStr", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> resourceJsonStr;
    protected Long securityKey;
    @XmlElementRef(name = "sessionId", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> sessionId;
    @XmlElementRef(name = "skin", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> skin;
    protected Boolean superAdmin;
    protected Boolean systemAdmin;
    @XmlElementRef(name = "timeZone", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeZone> timeZone;
    @XmlElementRef(name = "userAgentFrom", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userAgentFrom;
    @XmlElementRef(name = "userInfoJsonStr", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userInfoJsonStr;
    @XmlElementRef(name = "userSSOFrom", namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<String> userSSOFrom;
    protected Boolean v5Member;

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Set }{@code >}
     *     
     */
    public JAXBElement<Set> getAccessSystemMenu() {
        return accessSystemMenu;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Set }{@code >}
     *     
     */
    public void setAccessSystemMenu(JAXBElement<Set> value) {
        this.accessSystemMenu = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getAccountId() {
        return accountId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setAccountId(JAXBElement<Long> value) {
        this.accountId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdmin() {
        return admin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdmin(Boolean value) {
        this.admin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdministrator() {
        return administrator;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdministrator(Boolean value) {
        this.administrator = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAuditAdmin() {
        return auditAdmin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAuditAdmin(Boolean value) {
        this.auditAdmin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanSendSMS() {
        return canSendSMS;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanSendSMS(Boolean value) {
        this.canSendSMS = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Map }{@code >}
     *     
     */
    public JAXBElement<Map> getCustomizes() {
        return customizes;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Map }{@code >}
     *     
     */
    public void setCustomizes(JAXBElement<Map> value) {
        this.customizes = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getDepartmentId() {
        return departmentId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setDepartmentId(JAXBElement<Long> value) {
        this.departmentId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEtagRandom() {
        return etagRandom;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEtagRandom(JAXBElement<String> value) {
        this.etagRandom = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getExternalType() {
        return externalType;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setExternalType(JAXBElement<Integer> value) {
        this.externalType = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFontSize() {
        return fontSize;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFontSize(JAXBElement<String> value) {
        this.fontSize = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFromM1() {
        return fromM1;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFromM1(Boolean value) {
        this.fromM1 = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGroupAdmin() {
        return groupAdmin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGroupAdmin(Boolean value) {
        this.groupAdmin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getId() {
        return id;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setId(JAXBElement<Long> value) {
        this.id = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInternal() {
        return internal;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInternal(Boolean value) {
        this.internal = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getLevelId() {
        return levelId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setLevelId(JAXBElement<Long> value) {
        this.levelId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Locale }{@code >}
     *     
     */
    public JAXBElement<Locale> getLocale() {
        return locale;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Locale }{@code >}
     *     
     */
    public void setLocale(JAXBElement<Locale> value) {
        this.locale = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getLoginAccount() {
        return loginAccount;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setLoginAccount(JAXBElement<Long> value) {
        this.loginAccount = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLoginAccountName() {
        return loginAccountName;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLoginAccountName(JAXBElement<String> value) {
        this.loginAccountName = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLoginAccountShortName() {
        return loginAccountShortName;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLoginAccountShortName(JAXBElement<String> value) {
        this.loginAccountShortName = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getLoginLogId() {
        return loginLogId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setLoginLogId(JAXBElement<Long> value) {
        this.loginLogId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLoginName() {
        return loginName;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLoginName(JAXBElement<String> value) {
        this.loginName = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLoginSign() {
        return loginSign;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLoginSign(Integer value) {
        this.loginSign = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getLoginTimestamp() {
        return loginTimestamp;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setLoginTimestamp(JAXBElement<XMLGregorianCalendar> value) {
        this.loginTimestamp = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMainFrame() {
        return mainFrame;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMainFrame(JAXBElement<String> value) {
        this.mainFrame = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPassword() {
        return password;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPassword(JAXBElement<String> value) {
        this.password = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPlatformAdmin() {
        return platformAdmin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPlatformAdmin(Boolean value) {
        this.platformAdmin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getPostId() {
        return postId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setPostId(JAXBElement<Long> value) {
        this.postId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRemoteAddr(JAXBElement<String> value) {
        this.remoteAddr = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResourceJsonStr() {
        return resourceJsonStr;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResourceJsonStr(JAXBElement<String> value) {
        this.resourceJsonStr = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSecurityKey() {
        return securityKey;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSecurityKey(Long value) {
        this.securityKey = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSessionId() {
        return sessionId;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSessionId(JAXBElement<String> value) {
        this.sessionId = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSkin() {
        return skin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSkin(JAXBElement<String> value) {
        this.skin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuperAdmin() {
        return superAdmin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuperAdmin(Boolean value) {
        this.superAdmin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSystemAdmin(Boolean value) {
        this.systemAdmin = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public JAXBElement<TimeZone> getTimeZone() {
        return timeZone;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeZone }{@code >}
     *     
     */
    public void setTimeZone(JAXBElement<TimeZone> value) {
        this.timeZone = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserAgentFrom() {
        return userAgentFrom;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserAgentFrom(JAXBElement<String> value) {
        this.userAgentFrom = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserInfoJsonStr() {
        return userInfoJsonStr;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserInfoJsonStr(JAXBElement<String> value) {
        this.userInfoJsonStr = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserSSOFrom() {
        return userSSOFrom;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserSSOFrom(JAXBElement<String> value) {
        this.userSSOFrom = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isV5Member() {
        return v5Member;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setV5Member(Boolean value) {
        this.v5Member = value;
    }

}
