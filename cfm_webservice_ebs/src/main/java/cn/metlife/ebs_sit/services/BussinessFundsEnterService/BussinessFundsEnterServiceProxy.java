package cn.metlife.ebs_sit.services.BussinessFundsEnterService;

public class BussinessFundsEnterServiceProxy implements cn.metlife.ebs_sit.services.BussinessFundsEnterService.BussinessFundsEnterService {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.BussinessFundsEnterService.BussinessFundsEnterService bussinessFundsEnterService = null;
  
  public BussinessFundsEnterServiceProxy() {
    _initBussinessFundsEnterServiceProxy();
  }
  
  public BussinessFundsEnterServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initBussinessFundsEnterServiceProxy();
  }
  
  private void _initBussinessFundsEnterServiceProxy() {
    try {
      bussinessFundsEnterService = (new cn.metlife.ebs_sit.services.BussinessFundsEnterService.BussinessFundsEnterServiceServiceLocator()).getBussinessFundsEnterService();
      if (bussinessFundsEnterService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)bussinessFundsEnterService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)bussinessFundsEnterService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (bussinessFundsEnterService != null)
      ((javax.xml.rpc.Stub)bussinessFundsEnterService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.BussinessFundsEnterService.BussinessFundsEnterService getBussinessFundsEnterService() {
    if (bussinessFundsEnterService == null)
      _initBussinessFundsEnterServiceProxy();
    return bussinessFundsEnterService;
  }
  
  public java.lang.String dealFunds(java.lang.String requestXml) throws java.rmi.RemoteException{
    if (bussinessFundsEnterService == null)
      _initBussinessFundsEnterServiceProxy();
    return bussinessFundsEnterService.dealFunds(requestXml);
  }
  
  
}