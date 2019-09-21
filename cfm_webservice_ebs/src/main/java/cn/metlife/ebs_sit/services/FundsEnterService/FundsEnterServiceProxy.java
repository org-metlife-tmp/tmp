package cn.metlife.ebs_sit.services.FundsEnterService;

public class FundsEnterServiceProxy implements cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterService {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterService fundsEnterService = null;
  
  public FundsEnterServiceProxy() {
    _initFundsEnterServiceProxy();
  }
  
  public FundsEnterServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initFundsEnterServiceProxy();
  }
  
  private void _initFundsEnterServiceProxy() {
    try {
      fundsEnterService = (new cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterServiceServiceLocator()).getFundsEnterService();
      if (fundsEnterService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fundsEnterService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fundsEnterService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fundsEnterService != null)
      ((javax.xml.rpc.Stub)fundsEnterService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterService getFundsEnterService() {
    if (fundsEnterService == null)
      _initFundsEnterServiceProxy();
    return fundsEnterService;
  }
  
  public java.lang.String dealFunds(java.lang.String requestXml) throws java.rmi.RemoteException{
    if (fundsEnterService == null)
      _initFundsEnterServiceProxy();
    return fundsEnterService.dealFunds(requestXml);
  }
  
  
}