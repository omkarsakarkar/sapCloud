import com.sap.gateway.ip.core.customdev.util.Message;
import org.json.*;

def Message processData(Message message) {
	
	//Body 
	def body = message.getBody(String);
	JSONObject json = new JSONObject(body);
	String access_token = json.getString("access_token");
	
	//Headers 
	message.setHeader("access_token", access_token);
	
	//Properties  
	message.setProperty("access_token", access_token);
	
	return message;
} 
