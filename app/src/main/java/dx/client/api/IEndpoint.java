package dx.client.api;

/*
 -------------------------------------------------------------------------------------
 Protocol by JSon:
 -------------------------------------------------------------------------------------
 
 id = accessKey.privateId
 Forbid any ".", "-" symbol in accessKey or privateId. 

 -- open
 {'request' :'open', 'id':'key1.1'} the id is for this end point
 {'response':'open', 'returnCode': 1, 'reason':''} for success
 {'response':'open', 'returnCode':-1, 'reason':''} for fail

 -- connect
 {'request' :'connect', 'id':'2'} the id is for target end point with the same key.
 {'request' :'connect', 'id':'key2.2'} the id is for target end point.
 {'response':'connect', 'returnCode':1, 'reason':''} for success
 {'response':'connect', 'returnCode':-1, 'reason':''} for fail  
 {'event':'connect', 'id':'key1.1'} notify the end point 2 with caller's id
 {'event':'connect', 'id':'key2.2'} notify the caller with target end point's id

 -- disconnect
 {'request':'disconnect', 'id':'2'} the id is for target end point with the same key.
 {'request':'disconnect', 'id':'key2.2'} the id is for target end point.
 {'response':'disconnect', 'returnCode':1, 'reason':''} for success
 {'response':'disconnect', 'returnCode':-1, 'reason':''} for fail  
 {'event':'disconnect', 'id':'key1.1'} notify the end point 2 with caller's id 
 {'event':'disconnect', 'id':'key2.2'} notify the caller with target end point's id

 -- bridge close or remote end point close
 just close socket session for a close action.
 {'event':'disconnect', 'id':'key1.1'} notify another side end points with caller's id

 -- query
 {'request':'query'}
 {'response':'query', 'returnCode':1, 'reason':'', 'id':['key1.1','key2.2']} for success
 {'response':'query', 'returnCode':-1 , 'reason':''}  for fail

 -- event
 {'event':'disconnect', 'id':'key2.2'}
 {'event':'connect', 'id':'key2.2'}

 -------------------------------------------------------------------------------------

 */

/**
 * Client end point interface. The end user must implement this interface to access cloud service.
 * 
 * @author Sean Du
 * 
 */
public interface IEndpoint {
    int SERVICE_PROTOCOL_TCP = 1;
    int SERVICE_PROTOCOL_TCP_SSL = 2;
    int SERVICE_PROTOCOL_HTTP = 3;
    int SERVICE_PROTOCOL_HTTPS = 4;
    int SERVICE_PROTOCOL_WEBSOCKET = 5;
    int SERVICE_PROTOCOL_WEBSOCKET_SSL = 6;

    /**
     * Register an IEndpointListerner for callback.
     * @param listener IEndpointListener
     */
    void registerEndpointListener(IEndpointListener listener);
    
    
    /**
     * Open an end point.
     * @param address cloud service address for example ws://www.cloud-bridge.net:8080
     * @throws Exception exception threw if anything wrong happen
     */
    void open(String address) throws Exception;


    /**
     * Send byte data to the cloud service. 
     * @param data byte data
     * @throws Exception exception threw if anything wrong happen
     */
    void send(byte[] data) throws Exception ;
    
    /**
     * Send text message to the cloud service. 
     * @param message text information
     * @throws Exception exception threw if anything wrong happen
     */
    void send(String message) throws Exception;
    
    void ping() throws Exception;
    
    /**
     * Close the end point.
     * @throws Exception exception threw if anything wrong happen
     */
    void close() throws Exception ;

}


