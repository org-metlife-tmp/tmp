package cn.metlife.ebs_sit.services.CustomerAccountService;

public class CustomerAccountServiceProxy implements cn.metlife.ebs_sit.services.CustomerAccountService.CustomerAccountService {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.CustomerAccountService.CustomerAccountService customerAccountService = null;
  
  public CustomerAccountServiceProxy() {
    _initCustomerAccountServiceProxy();
  }
  
  public CustomerAccountServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCustomerAccountServiceProxy();
  }
  
  private void _initCustomerAccountServiceProxy() {
    try {
      customerAccountService = (new cn.metlife.ebs_sit.services.CustomerAccountService.CustomerAccountServiceServiceLocator()).getCustomerAccountService();
      if (customerAccountService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)customerAccountService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)customerAccountService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (customerAccountService != null)
      ((javax.xml.rpc.Stub)customerAccountService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.CustomerAccountService.CustomerAccountService getCustomerAccountService() {
    if (customerAccountService == null)
      _initCustomerAccountServiceProxy();
    return customerAccountService;
  }
  
  public java.lang.String getAccountInfo(java.lang.String requestXml) throws java.rmi.RemoteException{
    if (customerAccountService == null)
      _initCustomerAccountServiceProxy();
    return customerAccountService.getAccountInfo(requestXml);
  }
  
  
}