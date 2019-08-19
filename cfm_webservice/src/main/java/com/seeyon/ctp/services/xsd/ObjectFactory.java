
package com.seeyon.ctp.services.xsd;

import com.seeyon.ctp.common.authenticate.domain.xsd.User;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeyon.ctp.services.xsd package. 
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

    private final static QName _UserTokenId_QNAME = new QName("http://services.ctp.seeyon.com/xsd", "id");
    private final static QName _UserTokenNullToken_QNAME = new QName("http://services.ctp.seeyon.com/xsd", "nullToken");
    private final static QName _UserTokenBindingUser_QNAME = new QName("http://services.ctp.seeyon.com/xsd", "bindingUser");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeyon.ctp.services.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserToken }
     * 
     */
    public UserToken createUserToken() {
        return new UserToken();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ctp.seeyon.com/xsd", name = "id", scope = UserToken.class)
    public JAXBElement<String> createUserTokenId(String value) {
        return new JAXBElement<String>(_UserTokenId_QNAME, String.class, UserToken.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ctp.seeyon.com/xsd", name = "nullToken", scope = UserToken.class)
    public JAXBElement<UserToken> createUserTokenNullToken(UserToken value) {
        return new JAXBElement<UserToken>(_UserTokenNullToken_QNAME, UserToken.class, UserToken.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link User }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ctp.seeyon.com/xsd", name = "bindingUser", scope = UserToken.class)
    public JAXBElement<User> createUserTokenBindingUser(User value) {
        return new JAXBElement<User>(_UserTokenBindingUser_QNAME, User.class, UserToken.class, value);
    }

}
