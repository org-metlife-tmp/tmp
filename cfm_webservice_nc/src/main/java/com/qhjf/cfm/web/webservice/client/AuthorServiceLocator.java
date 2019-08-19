/**
 * AuthorServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.qhjf.cfm.web.webservice.client;

public class AuthorServiceLocator extends org.apache.axis.client.Service implements com.qhjf.cfm.web.webservice.client.AuthorService {

    public AuthorServiceLocator() {
    }


    public AuthorServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AuthorServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for authorPortName
    private java.lang.String authorPortName_address = "http://10.164.26.57:7090/newcomp/ws/author";

    public java.lang.String getauthorPortNameAddress() {
        return authorPortName_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String authorPortNameWSDDServiceName = "authorPortName";

    public java.lang.String getauthorPortNameWSDDServiceName() {
        return authorPortNameWSDDServiceName;
    }

    public void setauthorPortNameWSDDServiceName(java.lang.String name) {
        authorPortNameWSDDServiceName = name;
    }

    public com.qhjf.cfm.web.webservice.client.AuthorPortType getauthorPortName() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(authorPortName_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getauthorPortName(endpoint);
    }

    public com.qhjf.cfm.web.webservice.client.AuthorPortType getauthorPortName(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.qhjf.cfm.web.webservice.client.AuthorServiceSoapBindingStub _stub = new com.qhjf.cfm.web.webservice.client.AuthorServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getauthorPortNameWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setauthorPortNameEndpointAddress(java.lang.String address) {
        authorPortName_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.qhjf.cfm.web.webservice.client.AuthorPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.qhjf.cfm.web.webservice.client.AuthorServiceSoapBindingStub _stub = new com.qhjf.cfm.web.webservice.client.AuthorServiceSoapBindingStub(new java.net.URL(authorPortName_address), this);
                _stub.setPortName(getauthorPortNameWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("authorPortName".equals(inputPortName)) {
            return getauthorPortName();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://com.sinosoft.service.impl/webservice", "authorService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://com.sinosoft.service.impl/webservice", "authorPortName"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("authorPortName".equals(portName)) {
            setauthorPortNameEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
