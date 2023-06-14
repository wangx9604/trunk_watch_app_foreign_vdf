package dx.client.impl;

import com.xiaoxun.xun.utils.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import dx.client.api.IEndpointListener;
import dx.client.impl.ssl.EasySslClientContextFactory;
import dx.client.impl.ssl.SslClientContextFactory;
import dx.client.tool.CliCfg;

/*
TCP
64k size

+-----------------------------------------------------------------------------------------+
|0               1               2               3                       n                |
|0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 ... ... 0 0 1 2 3 4 5 6 7|
+--------------------------------+--------------------------------------------------------+
|             Length             |                        Pay load                        |
+--------------------------------+--------------------------------------------------------+
                 |                                            ^
                 |                                            |
                 +--------------------------------------------+

Length=0000 for ping/pong
*/

public class EndpointTcpImpl extends EndpointBaseImpl{

    private String remoteHost = "127.0.0.1";
    private int remotePort = 8081;
    
    private Socket clientSocket = null;
    private DataOutputStream outputStream = null;
    private DataInputStream  inputStream = null;
    

    private boolean isSSL = false;
    
    public EndpointTcpImpl(boolean _isSSL){
    	this.isSSL = _isSSL;
    }
    

    @Override
    public void open(String address) throws Exception {
        StringTokenizer st = new StringTokenizer(address, ":");
        remoteHost = st.nextToken();
        remotePort = Integer.parseInt(st.nextToken());
        
        if(this.isSSL){
	    	SSLSocketFactory sslSocketFactory;
	    	if(CliCfg.trustAll){
	    			sslSocketFactory = EasySslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}else{
	    		 	sslSocketFactory = SslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}
	        this.clientSocket = sslSocketFactory.createSocket();
        }
        else{
        	this.clientSocket = new Socket();
        }
        
        InetSocketAddress sockAddr = new InetSocketAddress(remoteHost, remotePort);
        
        clientSocket.connect(sockAddr,15000); // timeout is 15 seconds
        
        LogUtil.d("Local  = " + this.clientSocket.getLocalSocketAddress().toString());
        LogUtil.d("Remote = " + this.clientSocket.getRemoteSocketAddress().toString());
        if(this.clientSocket instanceof SSLSocket){
        	SSLSocket sslSocket = (SSLSocket)this.clientSocket;

            SSLSession ss = sslSocket.getSession();
            LogUtil.d("Cipher suite = " + ss.getCipherSuite());
            LogUtil.d("Protocol = " + ss.getProtocol());
        }

        
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
        
        if(this.outputStream == null || this.inputStream == null){
            throw new Exception("Socket io stream open failed!");
        }
        
        ReceiveData receiveData = new ReceiveData(this.inputStream);
        new Thread(receiveData,"ReceiveThread").start();
        InetSocketAddress address1 = (InetSocketAddress)this.clientSocket.getLocalSocketAddress();
        listener.onOpen(IEndpointListener.STATUS_CODE_SUCCESS, "Connected!",address1.getPort());
    }

   

    @Override
    public void close() throws Exception {
        closeConnection();
    }

    //**************************************************************************
    protected void sendText(String json) throws Exception{
        byte[] data = json.getBytes();
        ByteBuffer bf = ByteBuffer.allocate( 2 + data.length);
        bf.putShort((short)(data.length));
        bf.put(data);
        byte[] outBytes = bf.array();
        
        sendData(outBytes);
    }

    protected void sendBinary(byte[] data) throws Exception{
        sendData(data);
    }
    
	@Override
	public void ping() throws Exception{
		sendPing();
	}
    
    protected void sendPing() throws Exception{
        sendData(new byte[]{0,0});
    }
    
    protected void sendPong() throws Exception{
    	sendPing();
    }
    
    private synchronized void sendData(byte[] data) throws IOException {
    	  if(data != null && data.length>0 ){
        		outputStream.write(data);
      	}
    }
    
    private synchronized void closeConnection() throws IOException {
        this.clientSocket.close();
        listener.onClose(IEndpointListener.STATUS_CODE_SUCCESS, "endpoint closed", this.clientSocket.getPort());
    }
    //**************************************************************************
    
    public class ReceiveData implements Runnable {
        private DataInputStream in = null;
        
        public ReceiveData(DataInputStream input){
            this.in = input;
        }
        
        public void run() {
            try {
                while(true) {
                    int length = in.readUnsignedShort();
                    if(length==0){
                    	listener.onPing();
                    }
                    else{
	                    ByteBuffer bf = ByteBuffer.allocate(length);
	                    while(true) {
	                        byte b = in.readByte();
	                        bf.put(b);
	                        if(bf.position() == bf.capacity()){
	                            bf.rewind();
	                            byte[] data = new byte[bf.capacity()];
	                            bf.get(data);

                                InetSocketAddress address1 = (InetSocketAddress)clientSocket.getLocalSocketAddress();
	                            listener.onReceive(new String(data, Charset.forName("UTF-8")),address1.getPort());
	                            bf=null;
	                            break;
	                        }
	                    }
                    }
                }
            } catch (SocketException se) {
            	se.printStackTrace();
                listener.onError(IEndpointListener.STATUS_CODE_FAIL, se.getMessage(), clientSocket.getPort());
            } catch (Exception ex) {
            	ex.printStackTrace();
                listener.onError(IEndpointListener.STATUS_CODE_FAIL, "Error :" + ex.getMessage(), clientSocket.getPort());
            } 
        }
        
    }//Class ReceiveData
}

