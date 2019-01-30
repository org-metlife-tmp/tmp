/**
 * FundingPlatformPayBackServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cn.metlife.ebs_sit.services.FundingPlatformPayBack;

public class FundingPlatformPayBackServiceLocator extends org.apache.axis.client.Service implements cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackService {

    public FundingPlatformPayBackServiceLocator() {
    }


    public FundingPlatformPayBackServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FundingPlatformPayBackServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FundingPlatformPayBack
    private java.lang.String FundingPlatformPayBack_address = "http://ebs-sit.metlife.cn/services/FundingPlatformPayBack";

    public java.lang.String getFundingPlatformPayBackAddress() {
        return FundingPlatformPayBack_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FundingPlatformPayBackWSDDServiceName = "FundingPlatformPayBack";

    public java.lang.String getFundingPlatformPayBackWSDDServiceName() {
        return FundingPlatformPayBackWSDDServiceName;
    }

    public void setFundingPlatformPayBackWSDDServiceName(java.lang.String name) {
        FundingPlatformPayBackWSDDServiceName = name;
    }

    public cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack getFundingPlatformPayBack() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FundingPlatformPayBack_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFundingPlatformPayBack(endpoint);
    }

    public cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack getFundingPlatformPayBack(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackSoapBindingStub _stub = new cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackSoapBindingStub(portAddress, this);
            _stub.setPortName(getFundingPlatformPayBackWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFundingPlatformPayBackEndpointAddress(java.lang.String address) {
        FundingPlatformPayBack_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack.class.isAssignableFrom(serviceEndpointInterface)) {
                cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackSoapBindingStub _stub = new cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackSoapBindingStub(new java.net.URL(FundingPlatformPayBack_address), this);
                _stub.setPortName(getFundingPlatformPayBackWSDDServiceName());
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
        if ("FundingPlatformPayBack".equals(inputPortName)) {
            return getFundingPlatformPayBack();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ebs-sit.metlife.cn/services/FundingPlatformPayBack", "FundingPlatformPayBackService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ebs-sit.metlife.cn/services/FundingPlatformPayBack", "FundingPlatformPayBack"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FundingPlatformPayBack".equals(portName)) {
            setFundingPlatformPayBackEndpointAddress(address);
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
