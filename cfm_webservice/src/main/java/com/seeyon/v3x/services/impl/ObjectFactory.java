
package com.seeyon.v3x.services.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.seeyon.ctp.services.xsd.UserToken;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeyon.v3x.services.impl package. 
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

    private final static QName _ServiceExceptionServiceException_QNAME = new QName("http://impl.services.v3x.seeyon.com", "ServiceException");
    private final static QName _GetUserTokenUserName_QNAME = new QName("http://impl.services.v3x.seeyon.com", "userName");
    private final static QName _GetUserTokenPassword_QNAME = new QName("http://impl.services.v3x.seeyon.com", "password");
    private final static QName _GetUserTokenResponseReturn_QNAME = new QName("http://impl.services.v3x.seeyon.com", "return");
    private final static QName _ExceptionException_QNAME = new QName("http://impl.services.v3x.seeyon.com", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeyon.v3x.services.impl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Authenticate }
     * 
     */
    public Authenticate createAuthenticate() {
        return new Authenticate();
    }

    /**
     * Create an instance of {@link com.seeyon.v3x.services.impl.ServiceException }
     * 
     */
    public com.seeyon.v3x.services.impl.ServiceException createServiceException() {
        return new com.seeyon.v3x.services.impl.ServiceException();
    }

    /**
     * Create an instance of {@link GetUserToken }
     * 
     */
    public GetUserToken createGetUserToken() {
        return new GetUserToken();
    }

    /**
     * Create an instance of {@link AuthenticateResponse }
     * 
     */
    public AuthenticateResponse createAuthenticateResponse() {
        return new AuthenticateResponse();
    }

    /**
     * Create an instance of {@link GetUserTokenResponse }
     * 
     */
    public GetUserTokenResponse createGetUserTokenResponse() {
        return new GetUserTokenResponse();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link com.seeyon.v3x.services.xsd.ServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "ServiceException", scope = com.seeyon.v3x.services.impl.ServiceException.class)
    public JAXBElement<com.seeyon.v3x.services.xsd.ServiceException> createServiceExceptionServiceException(com.seeyon.v3x.services.xsd.ServiceException value) {
        return new JAXBElement<com.seeyon.v3x.services.xsd.ServiceException>(_ServiceExceptionServiceException_QNAME, com.seeyon.v3x.services.xsd.ServiceException.class, com.seeyon.v3x.services.impl.ServiceException.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "userName", scope = GetUserToken.class)
    public JAXBElement<String> createGetUserTokenUserName(String value) {
        return new JAXBElement<String>(_GetUserTokenUserName_QNAME, String.class, GetUserToken.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "password", scope = GetUserToken.class)
    public JAXBElement<String> createGetUserTokenPassword(String value) {
        return new JAXBElement<String>(_GetUserTokenPassword_QNAME, String.class, GetUserToken.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "return", scope = GetUserTokenResponse.class)
    public JAXBElement<UserToken> createGetUserTokenResponseReturn(UserToken value) {
        return new JAXBElement<UserToken>(_GetUserTokenResponseReturn_QNAME, UserToken.class, GetUserTokenResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "Exception", scope = Exception.class)
    public JAXBElement<Object> createExceptionException(Object value) {
        return new JAXBElement<Object>(_ExceptionException_QNAME, Object.class, Exception.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "return", scope = AuthenticateResponse.class)
    public JAXBElement<UserToken> createAuthenticateResponseReturn(UserToken value) {
        return new JAXBElement<UserToken>(_GetUserTokenResponseReturn_QNAME, UserToken.class, AuthenticateResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "userName", scope = Authenticate.class)
    public JAXBElement<String> createAuthenticateUserName(String value) {
        return new JAXBElement<String>(_GetUserTokenUserName_QNAME, String.class, Authenticate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.services.v3x.seeyon.com", name = "password", scope = Authenticate.class)
    public JAXBElement<String> createAuthenticatePassword(String value) {
        return new JAXBElement<String>(_GetUserTokenPassword_QNAME, String.class, Authenticate.class, value);
    }

}
