package cn.metlife.ebs_sit.services.FundingPlatformPayBack;

public class FundingPlatformPayBackProxy implements cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack fundingPlatformPayBack = null;
  
  public FundingPlatformPayBackProxy() {
    _initFundingPlatformPayBackProxy();
  }
  
  public FundingPlatformPayBackProxy(String endpoint) {
    _endpoint = endpoint;
    _initFundingPlatformPayBackProxy();
  }
  
  private void _initFundingPlatformPayBackProxy() {
    try {
      fundingPlatformPayBack = (new cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackServiceLocator()).getFundingPlatformPayBack();
      if (fundingPlatformPayBack != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fundingPlatformPayBack)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fundingPlatformPayBack)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fundingPlatformPayBack != null)
      ((javax.xml.rpc.Stub)fundingPlatformPayBack)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBack getFundingPlatformPayBack() {
    if (fundingPlatformPayBack == null)
      _initFundingPlatformPayBackProxy();
    return fundingPlatformPayBack;
  }
  
  public java.lang.String saveXML(java.lang.String xmlStr) throws java.rmi.RemoteException{
    if (fundingPlatformPayBack == null)
      _initFundingPlatformPayBackProxy();
    return fundingPlatformPayBack.saveXML(xmlStr);
  }
  
  
}