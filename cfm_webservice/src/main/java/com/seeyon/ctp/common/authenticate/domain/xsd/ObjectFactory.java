
package com.seeyon.ctp.common.authenticate.domain.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.seeyon.util.xsd.Locale;
import com.seeyon.util.xsd.Map;
import com.seeyon.util.xsd.Set;
import com.seeyon.util.xsd.TimeZone;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeyon.ctp.common.authenticate.domain.xsd package. 
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

    private final static QName _UserName_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "name");
    private final static QName _UserFontSize_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "fontSize");
    private final static QName _UserAccountId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "accountId");
    private final static QName _UserTimeZone_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "timeZone");
    private final static QName _UserPostId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "postId");
    private final static QName _UserSessionId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "sessionId");
    private final static QName _UserUserInfoJsonStr_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "userInfoJsonStr");
    private final static QName _UserLoginAccountName_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginAccountName");
    private final static QName _UserUserAgentFrom_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "userAgentFrom");
    private final static QName _UserId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "id");
    private final static QName _UserLoginTimestamp_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginTimestamp");
    private final static QName _UserMainFrame_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "mainFrame");
    private final static QName _UserEtagRandom_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "etagRandom");
    private final static QName _UserRemoteAddr_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "remoteAddr");
    private final static QName _UserLoginLogId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginLogId");
    private final static QName _UserCustomizes_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "customizes");
    private final static QName _UserPassword_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "password");
    private final static QName _UserLoginAccountShortName_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginAccountShortName");
    private final static QName _UserLevelId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "levelId");
    private final static QName _UserLoginName_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginName");
    private final static QName _UserSkin_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "skin");
    private final static QName _UserLocale_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "locale");
    private final static QName _UserAccessSystemMenu_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "accessSystemMenu");
    private final static QName _UserExternalType_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "externalType");
    private final static QName _UserResourceJsonStr_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "resourceJsonStr");
    private final static QName _UserLoginAccount_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "loginAccount");
    private final static QName _UserUserSSOFrom_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "userSSOFrom");
    private final static QName _UserDepartmentId_QNAME = new QName("http://domain.authenticate.common.ctp.seeyon.com/xsd", "departmentId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeyon.ctp.common.authenticate.domain.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "name", scope = User.class)
    public JAXBElement<String> createUserName(String value) {
        return new JAXBElement<String>(_UserName_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "fontSize", scope = User.class)
    public JAXBElement<String> createUserFontSize(String value) {
        return new JAXBElement<String>(_UserFontSize_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "accountId", scope = User.class)
    public JAXBElement<Long> createUserAccountId(Long value) {
        return new JAXBElement<Long>(_UserAccountId_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeZone }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "timeZone", scope = User.class)
    public JAXBElement<TimeZone> createUserTimeZone(TimeZone value) {
        return new JAXBElement<TimeZone>(_UserTimeZone_QNAME, TimeZone.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "postId", scope = User.class)
    public JAXBElement<Long> createUserPostId(Long value) {
        return new JAXBElement<Long>(_UserPostId_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "sessionId", scope = User.class)
    public JAXBElement<String> createUserSessionId(String value) {
        return new JAXBElement<String>(_UserSessionId_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "userInfoJsonStr", scope = User.class)
    public JAXBElement<String> createUserUserInfoJsonStr(String value) {
        return new JAXBElement<String>(_UserUserInfoJsonStr_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginAccountName", scope = User.class)
    public JAXBElement<String> createUserLoginAccountName(String value) {
        return new JAXBElement<String>(_UserLoginAccountName_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "userAgentFrom", scope = User.class)
    public JAXBElement<String> createUserUserAgentFrom(String value) {
        return new JAXBElement<String>(_UserUserAgentFrom_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "id", scope = User.class)
    public JAXBElement<Long> createUserId(Long value) {
        return new JAXBElement<Long>(_UserId_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginTimestamp", scope = User.class)
    public JAXBElement<XMLGregorianCalendar> createUserLoginTimestamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_UserLoginTimestamp_QNAME, XMLGregorianCalendar.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "mainFrame", scope = User.class)
    public JAXBElement<String> createUserMainFrame(String value) {
        return new JAXBElement<String>(_UserMainFrame_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "etagRandom", scope = User.class)
    public JAXBElement<String> createUserEtagRandom(String value) {
        return new JAXBElement<String>(_UserEtagRandom_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "remoteAddr", scope = User.class)
    public JAXBElement<String> createUserRemoteAddr(String value) {
        return new JAXBElement<String>(_UserRemoteAddr_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginLogId", scope = User.class)
    public JAXBElement<Long> createUserLoginLogId(Long value) {
        return new JAXBElement<Long>(_UserLoginLogId_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Map }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "customizes", scope = User.class)
    public JAXBElement<Map> createUserCustomizes(Map value) {
        return new JAXBElement<Map>(_UserCustomizes_QNAME, Map.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "password", scope = User.class)
    public JAXBElement<String> createUserPassword(String value) {
        return new JAXBElement<String>(_UserPassword_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginAccountShortName", scope = User.class)
    public JAXBElement<String> createUserLoginAccountShortName(String value) {
        return new JAXBElement<String>(_UserLoginAccountShortName_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "levelId", scope = User.class)
    public JAXBElement<Long> createUserLevelId(Long value) {
        return new JAXBElement<Long>(_UserLevelId_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginName", scope = User.class)
    public JAXBElement<String> createUserLoginName(String value) {
        return new JAXBElement<String>(_UserLoginName_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "skin", scope = User.class)
    public JAXBElement<String> createUserSkin(String value) {
        return new JAXBElement<String>(_UserSkin_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Locale }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "locale", scope = User.class)
    public JAXBElement<Locale> createUserLocale(Locale value) {
        return new JAXBElement<Locale>(_UserLocale_QNAME, Locale.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Set }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "accessSystemMenu", scope = User.class)
    public JAXBElement<Set> createUserAccessSystemMenu(Set value) {
        return new JAXBElement<Set>(_UserAccessSystemMenu_QNAME, Set.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "externalType", scope = User.class)
    public JAXBElement<Integer> createUserExternalType(Integer value) {
        return new JAXBElement<Integer>(_UserExternalType_QNAME, Integer.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "resourceJsonStr", scope = User.class)
    public JAXBElement<String> createUserResourceJsonStr(String value) {
        return new JAXBElement<String>(_UserResourceJsonStr_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "loginAccount", scope = User.class)
    public JAXBElement<Long> createUserLoginAccount(Long value) {
        return new JAXBElement<Long>(_UserLoginAccount_QNAME, Long.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "userSSOFrom", scope = User.class)
    public JAXBElement<String> createUserUserSSOFrom(String value) {
        return new JAXBElement<String>(_UserUserSSOFrom_QNAME, String.class, User.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://domain.authenticate.common.ctp.seeyon.com/xsd", name = "departmentId", scope = User.class)
    public JAXBElement<Long> createUserDepartmentId(Long value) {
        return new JAXBElement<Long>(_UserDepartmentId_QNAME, Long.class, User.class, value);
    }

}
