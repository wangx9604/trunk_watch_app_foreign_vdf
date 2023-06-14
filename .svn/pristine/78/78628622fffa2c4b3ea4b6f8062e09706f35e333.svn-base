package dx.client.impl;

import com.xiaoxun.xun.utils.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import dx.client.api.IEndpointListener;
import dx.client.impl.ssl.EasySslClientContextFactory;
import dx.client.impl.ssl.SslClientContextFactory;
import dx.client.tool.CliCfg;

/*

Cloud Bridge Service Platform Client Access Protocol for Web Socket

+-----------------------------------------+
|             Text Pay Load               | Text
+-----------------------------------------+
|             Binary Pay load             | Binary
+-----------------------------------------+

*/
public class EndpointWebsocketImpl extends EndpointBaseImpl {
    private String serviceUrl = "";
    private WsAdapter wsAdapter = null;

    private boolean isSSL = false;
    
    public EndpointWebsocketImpl(boolean _isSSL){
    	this.isSSL = _isSSL;
    }

    @Override
    public void open(String address) throws Exception {
        this.serviceUrl = address;

        this.wsAdapter = new WsAdapter(new URI(serviceUrl), new Draft_17() ); //draft 17 = rfc6455
        
        Socket socket = null;
        if(this.isSSL){
	    	SSLSocketFactory sslSocketFactory;
	    	if(CliCfg.trustAll){
	    			sslSocketFactory = EasySslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}else{
	    		 	sslSocketFactory = SslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}
	        socket = sslSocketFactory.createSocket();
            ((SSLSocket)socket).setUseClientMode(true);
	        this.wsAdapter.setSocket( socket );
        }
        
        this.wsAdapter.connectBlocking();  

        if (this.wsAdapter.getLocalSocketAddress() != null) {
            LogUtil.d("Local  = " + this.wsAdapter.getLocalSocketAddress().toString());
        }
        if (this.wsAdapter.getRemoteSocketAddress() != null) {
            LogUtil.d("Remote = " + this.wsAdapter.getRemoteSocketAddress().toString());
        }
        if(socket instanceof SSLSocket){
        	SSLSocket sslSocket = (SSLSocket)socket;
            //sslSocket.setNeedClientAuth(true);
            SSLSession ss = sslSocket.getSession();
            LogUtil.d("Cipher suite = " + ss.getCipherSuite());
            LogUtil.d("Protocol = " + ss.getProtocol());
        }
        
    }


    @Override
    public void close() throws Exception {
        if (this.wsAdapter != null && this.wsAdapter.getConnection() != null) {
            this.wsAdapter.close();
        } else {
            throw new Exception("The session is null!");
        }
    }

    //**************************************************************************
    protected void sendText(String json) throws Exception{
        if (this.wsAdapter != null && this.wsAdapter.getConnection() != null && this.wsAdapter.getConnection().isOpen()) {
            this.wsAdapter.send(json); 
            
        } else {
            throw new Exception("The session is null or it isn't open!");
        }
    }
    
    protected void sendBinary(byte[] data)  throws Exception{
        if (this.wsAdapter != null && this.wsAdapter.getConnection() != null && this.wsAdapter.getConnection().isOpen()) {
            this.wsAdapter.send(data);
        } else {
            throw new Exception("The session is null or it isn't open!");
        }
    }
    
    @Override
    public void ping() throws Exception{
        sendPing();
    }
    
    protected void sendPing() throws Exception{
        FramedataImpl1 framePing = new FramedataImpl1();
        framePing.setOptcode( Opcode.PING );
        framePing.setFin(true);
        framePing.setPayload(ByteBuffer.allocate(0));
        this.wsAdapter.sendFrame(framePing);
    }
    
    protected void sendPong() throws Exception{
        FramedataImpl1 framePong = new FramedataImpl1();
        framePong.setOptcode( Opcode.PONG );
        this.wsAdapter.sendFrame(framePong);
    }
    
    //**************************************************************************
    public class WsAdapter extends WebSocketClient {

				public WsAdapter(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        public WsAdapter(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onClose(int statusCode, String reason, boolean arg2, int port) {
            listener.onClose(statusCode, reason, port);
        }
        
        @Override
        public void onOpen(ServerHandshake shs,int port) {
            listener.onOpen(IEndpointListener.STATUS_CODE_SUCCESS, "Connected!",port);
        }

        @Override
        public void onError(Exception ex, int port) {
            if (listener != null) {
                listener.onError(IEndpointListener.STATUS_CODE_FAIL, ex.getMessage(), port);
            }
        }
        
        @Override
        public void onMessage(ByteBuffer byteBuffer) {
            listener.onReceive(byteBuffer.array());
        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {
            listener.onPing();
        }

        @Override
        public void onMessage(String message,int port) {
            listener.onReceive(message,port);
        }
        
    }//Class WsAdapter
    
}
