package com.example.test.wenzi;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.test.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
public class Check {

//	public  String checkFile(Bitmap bitmap) throws URISyntaxException, IOException {
//		   ByteArrayOutputStream os=new ByteArrayOutputStream();
//		   bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
//		   byte[] bytes=os.toByteArray();
//		   byte[] encode=Base64.encode(bytes,Base64.DEFAULT);
//		   String encodeStr=new String(encode);
//	       return encode;
//	   }
}
