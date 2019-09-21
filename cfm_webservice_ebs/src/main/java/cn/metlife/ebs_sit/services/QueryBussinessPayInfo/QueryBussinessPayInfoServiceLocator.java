/**
 * QueryBussinessPayInfoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cn.metlife.ebs_sit.services.QueryBussinessPayInfo;

public class QueryBussinessPayInfoServiceLocator extends org.apache.axis.client.Service implements cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoService {

    public QueryBussinessPayInfoServiceLocator() {
    }


    public QueryBussinessPayInfoServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QueryBussinessPayInfoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QueryBussinessPayInfo
    private java.lang.String QueryBussinessPayInfo_address = "http://ebs-sit.metlife.cn/services/QueryBussinessPayInfo";

    public java.lang.String getQueryBussinessPayInfoAddress() {
        return QueryBussinessPayInfo_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QueryBussinessPayInfoWSDDServiceName = "QueryBussinessPayInfo";

    public java.lang.String getQueryBussinessPayInfoWSDDServiceName() {
        return QueryBussinessPayInfoWSDDServiceName;
    }

    public void setQueryBussinessPayInfoWSDDServiceName(java.lang.String name) {
        QueryBussinessPayInfoWSDDServiceName = name;
    }

    public cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo getQueryBussinessPayInfo() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QueryBussinessPayInfo_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQueryBussinessPayInfo(endpoint);
    }

    public cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo getQueryBussinessPayInfo(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoSoapBindingStub _stub = new cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoSoapBindingStub(portAddress, this);
            _stub.setPortName(getQueryBussinessPayInfoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQueryBussinessPayInfoEndpointAddress(java.lang.String address) {
        QueryBussinessPayInfo_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo.class.isAssignableFrom(serviceEndpointInterface)) {
                cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoSoapBindingStub _stub = new cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoSoapBindingStub(new java.net.URL(QueryBussinessPayInfo_address), this);
                _stub.setPortName(getQueryBussinessPayInfoWSDDServiceName());
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
        if ("QueryBussinessPayInfo".equals(inputPortName)) {
            return getQueryBussinessPayInfo();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ebs-sit.metlife.cn/services/QueryBussinessPayInfo", "QueryBussinessPayInfoService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ebs-sit.metlife.cn/services/QueryBussinessPayInfo", "QueryBussinessPayInfo"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("QueryBussinessPayInfo".equals(portName)) {
            setQueryBussinessPayInfoEndpointAddress(address);
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
