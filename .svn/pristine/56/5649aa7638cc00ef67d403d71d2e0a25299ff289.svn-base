package dx.client.tool;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dx.client.api.EndpointFactory;
import dx.client.api.IEndpoint;
import dx.client.api.IEndpointListener;

public class EpCLI implements IEndpointListener{
    private IEndpoint cep = null;  // Client Endpoint
    
    public static long startTime = 0;
    
    public static String SID = "";
    
    public EpCLI(){
        super();
    }

    public IEndpoint getCep() {
        return cep;
    }

    public void setCep(IEndpoint cep) {
        this.cep = cep;
    }

    public void sendAndWait(String text) throws Exception{
        if(this.cep!=null){
        	if((SID != null) && (!SID.isEmpty())){
        		text = text.replaceFirst("SSIIDD", SID);
        	}
        	
        	LogUtil.i(text);
            this.cep.send(text);
        }
    }
    
    public void sendPing() throws Exception{
        if(this.cep!=null){
            this.cep.ping();
        }
    }
    
    
    public String loadJsonFile(String fileDir) throws Exception{
    	StringBuffer sb = new StringBuffer();
    	
    	try {  
    	    BufferedReader reader = new BufferedReader(new FileReader(fileDir));  
    	    String line = reader.readLine();  
    	    while(line != null){  
    	        sb.append(line.trim());
    	        line = reader.readLine();
    	    }  
    	    reader.close();  
    	} catch (FileNotFoundException e) {  
    	    e.printStackTrace();  
    	} catch (IOException e) {  
    	    e.printStackTrace();  
    	}  
    	
    	if((SID != null) && (!SID.isEmpty())){
    		String result = sb.toString();
    		result = result.replaceFirst("SSIIDD", SID);
    		return result;
    	}
    	else{
    		return sb.toString();
    	}
    }
    
    

    public static void main(String[] args) throws Exception {
    	
    	CliCfg.loadConfig();
    	
        EpCLI epCLI = new EpCLI();
        String protocol = "ws";
        String address = CliCfg.ws;
        String display = "websocket";
        
        if(args.length>=1){
        	protocol = args[0];
        }
        
        IEndpoint cep = null;
        if(protocol.equalsIgnoreCase("tcp")){
            protocol = "tcp";
            address = CliCfg.tcp;
            display = "TCP";
            cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_TCP); 
        }
        else  if(protocol.equalsIgnoreCase("ssl")){
            protocol = "ssl";
            address = CliCfg.ssl;
            display = "TCP on SSL";
            cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_TCP_SSL); 
        }
        else  if(protocol.equalsIgnoreCase("wss")){
            protocol = "wss";
            address = CliCfg.wss;
            display = "Secure WebSocket";
            cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_WEBSOCKET_SSL); 
        }
        else  if(protocol.equalsIgnoreCase("http")){
            protocol = "http";
            address = CliCfg.http;
            display = "Http";
            cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_HTTP); 
        }
        else  if(protocol.equalsIgnoreCase("https")){
            protocol = "https";
            address = CliCfg.https;
            display = "Https";
            cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_HTTPS); 
        }
        else{
            protocol = "ws";
            address = CliCfg.ws;
            display = "WebSocket";
        	cep = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_WEBSOCKET); 
        }
        
        epCLI.setCep(cep);
        cep.registerEndpointListener(epCLI);
        
        LogUtil.i("Type ? for help.");
        LogUtil.i("Connect to [ " + address + " ] with " + display + ".");

        
        epCLI.cep.open(address);
        
        Console console =  System.console();
        while (true) {
            String cmd = console.readLine(">");
            if( cmd==null || cmd.isEmpty()) continue;

            try{
            	cmd = cmd.trim();
            	
                if ( cmd.startsWith("?")) {
                    LogUtil.i("---------------------------------------------------------------------");
                    LogUtil.i("  Command List                                                       ");
                    LogUtil.i("---------------------------------------------------------------------");
                    LogUtil.i("  Help          ?                                                    ");
                    LogUtil.i("  Exit          q!                                                   ");
                    LogUtil.i("---------------------------------------------------------------------");
                    LogUtil.i("  {'CID':1}     Just input json string and press Enter for sending. ");
                    LogUtil.i("  list          List all testcases.");
                    LogUtil.i("  load <file>   Load and show testcase file.");
                    LogUtil.i("  send <file>   Load testcase file and send.");
                    LogUtil.i("  ping          Send ping to server.");
                    LogUtil.i("---------------------------------------------------------------------");
                    LogUtil.i("  Testcase examples : ");
                    LogUtil.i("  send testcase\\UserLogin.json");
                    LogUtil.i("  send testcase\\DeviceLogin.json");
                    LogUtil.i("  load testcase\\UserLogin.json");
                    LogUtil.i("  load testcase\\DeviceLogin.json");
                    LogUtil.i("---------------------------------------------------------------------");
                    LogUtil.i("  Service address information is in cli.cfg file.                    ");
                    LogUtil.i("  For more helpful information in www.xxx.net                        ");
                    LogUtil.i("---------------------------------------------------------------------");
                }
                else if (cmd.equalsIgnoreCase("q!")) {
                    epCLI.cep.close();
                    LogUtil.i("Exit!");
                    System.exit(0);
                } 
                else if (cmd.startsWith("list")) {
                	String profixFile = System.getProperty("user.dir");
                	LogUtil.i(profixFile);
                	File rootFile = new File("testcase");

                	File[] files = rootFile.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        String fullName = files[i].getAbsolutePath();
                        
                        if(files[i].isDirectory()){
                        	File[] subFiles = files[i].listFiles();
                        	for (int j = 0; j < subFiles.length; j++) {
                        		String subFullNme = subFiles[j].getAbsolutePath();
                        		subFullNme = subFullNme.substring(profixFile.length()+1);
                        		LogUtil.i("load " + subFullNme);
                        	}
                        }
                        else{
                        	fullName = fullName.substring(profixFile.length()+1);
                        	LogUtil.i("load " + fullName);
                        }
                    }
                } 
                else if (cmd.startsWith("send")) {
                	EpCLI.startTime = System.currentTimeMillis();
                    String strMsg = epCLI.loadJsonFile(cmd.substring(5).trim());
                    epCLI.sendAndWait(strMsg);
                } 
                else if (cmd.startsWith("load")) {
                	EpCLI.startTime = System.currentTimeMillis();
                	String strMsg = epCLI.loadJsonFile(cmd.substring(4).trim());
                	JSONObject jo = (JSONObject)JSONValue.parse(strMsg); 
                	LogUtil.i(jo.toJSONString());
                } 
                else if (cmd.startsWith("ping")) {
                	EpCLI.startTime = System.currentTimeMillis();
                	epCLI.sendPing();
                } 
                else{
                	EpCLI.startTime = System.currentTimeMillis();
                    epCLI.sendAndWait(cmd);
                }
            }
            catch(Exception ex){
            	ex.printStackTrace();
                LogUtil.i(ex.getMessage());
            }

        }
    }
    
    //**************************************************************************
    
    @Override
    public void onOpen(int status, String reason,int port) {
        LogUtil.i(reason + ", Status=" + status);
        LogUtil.i("Ready!");
    }

    @Override
    public void onReceive(byte[] buf) {
    	long usedTime = System.currentTimeMillis() - EpCLI.startTime;
    	String message = new String(buf);
    	
    	JSONObject jo = (JSONObject)JSONValue.parse(message);  
		if(jo.get("SID")!=null){
			EpCLI.SID = (String)jo.get("SID");
		}
		
    	JsonObject obj = new GsonBuilder().disableHtmlEscaping().create().fromJson(message, JsonObject.class);
    	LogUtil.i(new GsonBuilder().setPrettyPrinting().create().toJson(obj) + " \r\n- " + usedTime + " ms");
    	LogUtil.i("---------------------------------------------------------------------");
        EpCLI.startTime = 0;
    }

    @Override
    public void onReceive(String message,int port) {
    	long usedTime = System.currentTimeMillis() - EpCLI.startTime;
    	JSONObject jo = (JSONObject)JSONValue.parse(message);  
		if(jo.get("SID")!=null){
			EpCLI.SID = (String)jo.get("SID");
		}
    	
    	JsonObject obj = new GsonBuilder().disableHtmlEscaping().create().fromJson(message, JsonObject.class);
    	LogUtil.i(new GsonBuilder().setPrettyPrinting().create().toJson(obj) + " \r\n- " + usedTime + " ms");
    	LogUtil.i("---------------------------------------------------------------------");
        EpCLI.startTime = 0;
    }
    
    @Override
    public void onPing() {
    	long usedTime = System.currentTimeMillis() - EpCLI.startTime;

    	LogUtil.i("pong" + " \r\n- " + usedTime + " ms");
    	LogUtil.i("---------------------------------------------------------------------");
        EpCLI.startTime = 0;
    }
    
    @Override
    public void onClose(int status, String reason, int port) {
        LogUtil.i(reason + ", Status=" + status + " port= " + port);
        LogUtil.i("Exit!");
        System.exit(0);
    }

    @Override
    public void onError(int errorCode, String reason, int port){
        LogUtil.i(reason + ", errorCode=" + errorCode + " port= " + port);
        LogUtil.i("Exit!");
        System.exit(0);
    }

}
