package cn.metlife.ebs_sit.services.AdvanceReceiptStatusService;

public class AdvanceReceiptStatusServiceProxy implements cn.metlife.ebs_sit.services.AdvanceReceiptStatusService.AdvanceReceiptStatusService {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.AdvanceReceiptStatusService.AdvanceReceiptStatusService advanceReceiptStatusService = null;
  
  public AdvanceReceiptStatusServiceProxy() {
    _initAdvanceReceiptStatusServiceProxy();
  }
  
  public AdvanceReceiptStatusServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initAdvanceReceiptStatusServiceProxy();
  }
  
  private void _initAdvanceReceiptStatusServiceProxy() {
    try {
      advanceReceiptStatusService = (new cn.metlife.ebs_sit.services.AdvanceReceiptStatusService.AdvanceReceiptStatusServiceServiceLocator()).getAdvanceReceiptStatusService();
      if (advanceReceiptStatusService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)advanceReceiptStatusService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)advanceReceiptStatusService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (advanceReceiptStatusService != null)
      ((javax.xml.rpc.Stub)advanceReceiptStatusService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.AdvanceReceiptStatusService.AdvanceReceiptStatusService getAdvanceReceiptStatusService() {
    if (advanceReceiptStatusService == null)
      _initAdvanceReceiptStatusServiceProxy();
    return advanceReceiptStatusService;
  }
  
  public java.lang.String getStatusInfo(java.lang.String requestXml) throws java.rmi.RemoteException{
    if (advanceReceiptStatusService == null)
      _initAdvanceReceiptStatusServiceProxy();
    return advanceReceiptStatusService.getStatusInfo(requestXml);
  }
  
  
}