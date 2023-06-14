package dx.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Compress or uncompress data util.
 * 
 * @author Sean
 *
 */
public class ZipUtil {
	
	/**Compress data with zip.
	 * @param data plain data.
	 * @return compressed data.
	 * @throws IOException
	 */
	public static byte[] zip(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			return data;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		
		return out.toByteArray();
	}

	/**
	 * Uncompress data with zip. 
	 * @param data compressed data.
	 * @return plain data.
	 * @throws IOException
	 */
	public static byte[] unzip(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			return data;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream( data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
	
	
	/**
	 * Compress and encode with base64.
	 * @param plain plain text.
	 * @return compressed and coded base64 text.
	 * @throws IOException
	 */
	public static String zipAndBase64(String plain) throws IOException{
    	//zip
    	byte[] bCompressed = ZipUtil.zip(plain.getBytes());
    	
    	//base64
		byte[] bBase64 = Base64.encode(bCompressed);
		
		String strCoded = new String(bBase64);
		return strCoded;
	}
	
	/**
	 * Compress and encode with base64.
	 * @param plain plain byte array.
	 * @return compressed and coded base64 text.
	 * @throws IOException
	 */
	public static String zipAndBase64(byte[] plain) throws IOException{
    	//zip
    	byte[] bCompressed = ZipUtil.zip(plain);
    	
    	//base64
		byte[] bBase64 = Base64.encode(bCompressed);
		
		String strCoded = new String(bBase64);
		return strCoded;
	}
	
	
	/**
	 * Decode with base64 and uncompress.
	 * @param coded compressed and coded with base64 text.
	 * @return decode with base64 and uncompressed data.
	 * @throws IOException
	 */
	public static String unbase64AndUnzip(String coded) throws IOException{
    	//unbase64
    	byte[] bUnBase64 = Base64.decode(coded.getBytes());
    	
		//unzip
		byte[] bUncompressed = ZipUtil.unzip(bUnBase64);
		String strPlain = new String(bUncompressed);
		
		return strPlain;
	}
	
	/**
	 * Decode with base64 and uncompress.
	 * @param coded compressed and coded with base64 text.
	 * @return decode with base64 and uncompressed byte array data.
	 * @throws IOException
	 */
	public static byte[] unbase64AndUnzip2ByteArray(String coded) throws IOException{
    	//unbase64
    	byte[] bUnBase64 = Base64.decode(coded.getBytes());
    	
		//unzip
		byte[] bUncompressed = ZipUtil.unzip(bUnBase64);
		
		return bUncompressed;
	}
	
	
	/*
	public static void main(String[] args) throws IOException {
    	
    	String plain = "[123.72, 8374.1, 921,921, 3823.34]";
    	LogUtil.d(plain);
    	
    	String coded = ZipUtil.zipAndBase64(plain);
		
    	LogUtil.d(coded);
		
    	String uncoded = ZipUtil.unbase64AndUnzip(coded);
	
		LogUtil.d(uncoded);
		
		LogUtil.d(ZipUtil.unbase64AndUnzip("H4sIAAAAAAAAAIs2NDLWMzfSsTA2N9Ez1LE0gmBjC6CwsUksABhQ9s8fAAAA"));
	}
	*/

}
