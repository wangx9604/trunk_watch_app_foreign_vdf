package dx.client.api;


/**
 * Client end point callback interface. The end user must implement this interface to get callback from cloud service.
 * @author Sean Du
 *
 */
public interface IEndpointListener {

    /**
     * Common successful status
     */
    int STATUS_CODE_SUCCESS = 1;
    
    /**
     * Common fail status
     */
    int STATUS_CODE_FAIL = -1;


    /**
     * This method will be called when the end point is opened or not.
     * 
     * @param status the value is STATUS_CODE_SUCCESS means success, other minus value means fail.
     * @param reason return text reason.
     */
    void onOpen(int status, String reason, int port);

    /**
     * If the connection is closed, this method will be called. 
     * @param status status code for reason.
     * @param reason text reason.
     */
    void onClose(int status, String reason, int port);


    /**
     * Receive binary data from the cloud server.
     * @param data byte data.
     */
    void onReceive(byte[] data);


    /**
     * Receive text message from the cloud server. 
     * @param message text message.
     */
    void onReceive(String message, int port);
    
    void onPing();
    
    /**
     * Error message .
     * @param errorCode error code.
     * @param reason error reason information.
     */
    void onError(int errorCode, String reason, int port);

}
