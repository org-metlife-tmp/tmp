package cn.metlife.ebs_sit.services.QueryBussinessPayInfo;

public class QueryBussinessPayInfoProxy implements cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo {
  private String _endpoint = null;
  private cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo queryBussinessPayInfo = null;
  
  public QueryBussinessPayInfoProxy() {
    _initQueryBussinessPayInfoProxy();
  }
  
  public QueryBussinessPayInfoProxy(String endpoint) {
    _endpoint = endpoint;
    _initQueryBussinessPayInfoProxy();
  }
  
  private void _initQueryBussinessPayInfoProxy() {
    try {
      queryBussinessPayInfo = (new cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoServiceLocator()).getQueryBussinessPayInfo();
      if (queryBussinessPayInfo != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)queryBussinessPayInfo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)queryBussinessPayInfo)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (queryBussinessPayInfo != null)
      ((javax.xml.rpc.Stub)queryBussinessPayInfo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfo getQueryBussinessPayInfo() {
    if (queryBussinessPayInfo == null)
      _initQueryBussinessPayInfoProxy();
    return queryBussinessPayInfo;
  }
  
  public java.lang.String queryInfo(java.lang.String requestXml) throws java.rmi.RemoteException{
    if (queryBussinessPayInfo == null)
      _initQueryBussinessPayInfoProxy();
    return queryBussinessPayInfo.queryInfo(requestXml);
  }
  
  
}