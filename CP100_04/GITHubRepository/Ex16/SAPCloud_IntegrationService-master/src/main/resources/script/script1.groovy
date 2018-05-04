import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import groovy.transform.Field;


// this script is not part of standard HCI


@Field String IFLOW_NAME = 'toolbox_splitter_aggregator_in_memory'

@Field String FILE_LOGGING_MODE = 'ALWAYS';  //ALWAYS, NEVER , PROPERTY
@Field String MPL_LOGGING_MODE = 'ALWAYS';   //ALWAYS, NEVER, PROPERTY

@Field List EXCLUDE_PROPERTIES = ['SAP_MessageProcessingLogID', 'SAP_MonitoringStateProperties', 'MplMarkers', 'SAP_MessageProcessingLog'];
@Field List EXCLUDE_HEADERS = [''];

@Field String LOG_HEADERS       = 'YES';   //YES / NO
@Field String LOG_PROPERTIES    = 'YES';   //YES / NO
@Field String LOG_BODY_INFO     = 'YES';   //YES / NO
@Field String LOG_EXCEPTION     = 'YES';   //YES / NO
@Field String LOG_OTHER         = 'YES';   //YES / NO
@Field String LOG_BODY          = 'YES';   //YES / NO
@Field String MODIFY_EXCHANGE   = 'NO';   //YES / NO

@Field String PROPERTY_INVOCATION_COUNTER = 'SAP_METVIEWER_INVOCATION_COUNTER'
@Field String PROPERTY_LAST_TIMESTAMP = 'SAP_METVIEWER_LAST_TIMESTAMP'



// if you use this method, you need to copy the script for every usage and adapt the filename (000)
def Message processData(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        
        //def props = message.getProperties();
        //def some_value = props.get('some_property');
        
        processHeadersAndProperties(    IFLOW_NAME+"_000",           message);
        processBody(                    IFLOW_NAME+"_000_payload",   message);
        
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}


// you can use _010, _020 etc methods to reuse same script from multiple places in the iflow
def Message log_010(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        increaseInternalHeaders(message);
        increaseInternalProperties(message);
        processHeadersAndProperties(    IFLOW_NAME+'_010_{COUNTER}',           message);
        processBody(                    IFLOW_NAME+'_010_{COUNTER}_payload',   message);

    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}

def Message log_020(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        increaseInternalHeaders(message);
        increaseInternalProperties(message);
        processHeadersAndProperties(    IFLOW_NAME+"_020_{COUNTER}",           message);
        processBody(                    IFLOW_NAME+"_020_{COUNTER}_payload",   message);
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}


def Message log_030(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        increaseInternalHeaders(message);
        increaseInternalProperties(message);
        processHeadersAndProperties(    IFLOW_NAME+"_030_{COUNTER}",           message);
        processBody(                    IFLOW_NAME+"_030_{COUNTER}_payload",   message);
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}

def Message log_040(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        increaseInternalHeaders(message);
        increaseInternalProperties(message);
        processHeadersAndProperties(    IFLOW_NAME+"_040_{COUNTER}",           message);
        processBody(                    IFLOW_NAME+"_040_{COUNTER}_payload",   message);
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}



def Message error(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        processData(IFLOW_NAME+"_error", message);
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}


def Message processDataWithCounterIncreasing(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        processDataIncreasing("LOG_", "COUNTER", message);
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
    return message;
}


def boolean isTrue(String str) {
    if ('ALWAYS'.equalsIgnoreCase(str)) {
        return true;
    } else if ('YES'.equalsIgnoreCase(str)) {
       return true;
    } else ('ON'.equalsIgnoreCase(str)) {
       return true;
    }
    return false;
}    




def void increaseInternalHeaders(Message message) {

   def props = message.getHeaders();
   def StringBuffer counter = props.get(PROPERTY_INVOCATION_COUNTER);
   
   if (counter==null) {
        counter = new StringBuffer();
        counter.append("0");
        message.setHeader(PROPERTY_INVOCATION_COUNTER, counter);
    }
    
    int cnt = Integer.valueOf(counter.toString());
    cnt = cnt+1;
    def counterS = ""+cnt;
    counter.setLength(0);
    counter.append(counterS);
}

def void increaseInternalProperties(Message message) {

   def props = message.getProperties();
   def StringBuffer counter = props.get(PROPERTY_INVOCATION_COUNTER);
   
   if (counter==null) {
        counter = new StringBuffer();
        counter.append("0");
        props.put(PROPERTY_INVOCATION_COUNTER, counter);
    }
    
    int cnt = Integer.valueOf(counter.toString());
    cnt = cnt+1;
    def counterS = ""+cnt;
    counter.setLength(0);
    counter.append("0");
    
}



def Message processDataIncreasing(String prefix, String propertyName, Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    def props = message.getProperties();
    def StringBuffer counter = props.get(propertyName);
    if (counter==null) {
        counter = new StringBuffer();
        counter.append("0");
        message.setProperty(propertyName, counter);
    }
    
    int cnt = Integer.valueOf(counter.toString());
    cnt = cnt+1;
    def counterS = ""+cnt;
    counter.setLength(0);
    counter.append(counterS);
    processData(prefix+"_"+counter, message);
    return message;
}





// use this method if you want to have an counter in file name
// this method does not increase the counter after usage
def Message processDataWithCounter(Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    try {
        def props = message.getProperties();
        def String counter = props.get("COUNTER");
        if ((counter==null)||("".equals(counter))) {
            counter = "0";
        }
        return processData("LOG_"+counter, message);
        
    } catch (Exception ex) {
        log.error("processData error",ex);
    }
}


def Message processData(String prefix, Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    try {
        processBody(prefix+"_payload", message);
        processHeadersAndProperties(prefix, message);
    } catch (Exception ex00) {
        log.error("processData error",ex00)
        StringWriter sw = new StringWriter();
        ex00.printStackTrace(new PrintWriter(sw));
        log.error(sw.toString());
    }
    return message;
}


def Map excludeEntries(Map map, List excluded) {
    def newMap = new HashMap();
    newMap.putAll(map);
    newMap.keySet().removeAll(excluded);
    return newMap;
    
}



def void processBody(String prefix_with_vars, Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    def byte[] body_bytes = null;
    try {
        
        
        def prefix = resolveVariables(prefix_with_vars, message);
        
        def enable = false;
    
        if ('YES'.equalsIgnoreCase(LOG_BODY)) {
            enable = true;
        }
            
        if (!enable) return;
        
        
        if (message==null) {
            body_bytes = new byte[0];
        } else if (message.getBody() == null) {
            body_bytes = new byte[0];
        } else {
            body_bytes = message.getBody(byte[].class);
        }
        
        def props = message.getProperties();
        def property_ENABLE_MPL_LOGGING = props.get("ENABLE_MPL_LOGGING");
        def property_ENABLE_FILE_LOGGING = props.get("ENABLE_FILE_LOGGING");
        
        def mpl_enabled = false;
        if ("ALWAYS".equalsIgnoreCase(MPL_LOGGING_MODE)) {
            mpl_enabled = true;
        } else if ("YES".equalsIgnoreCase(MPL_LOGGING_MODE)) {
           mpl_enabled = true;
        } else ("PROPERTY".equalsIgnoreCase(MPL_LOGGING_MODE)) {
            if ("TRUE".equalsIgnoreCase(property_ENABLE_MPL_LOGGING)) {
                mpl_enabled = true;
            }
        }
        
        
        def file_enabled = false;
        if ("ALWAYS".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            file_enabled = true;
        } else if ("YES".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            mpl_enabled = true;
        } else ("PROPERTY".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            if ("TRUE".equalsIgnoreCase(property_ENABLE_FILE_LOGGING)) {
                file_enabled = true;
            }
        }
        
        
        
        if (mpl_enabled) {
            def messageLog = messageLogFactory.getMessageLog(message);
            def mpl_prefix = prefix;
            if (prefix.startsWith(IFLOW_NAME+'_')) {
               mpl_prefix = prefix.substring(IFLOW_NAME.length()+1);
            }
            messageLog.addAttachmentAsString(mpl_prefix, new String(body_bytes), "text/plain");
        }
        
        if (file_enabled) {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            def task = {c -> pool.submit( c as Callable)}
            task{saveFile(""+prefix+".xml", body_bytes)}
        }
        
        
    } catch (Exception ex01) {
        log.error("cannot save body",ex01);
        StringWriter sw = new StringWriter();
        ex01.printStackTrace(new PrintWriter(sw));
        log.info(sw.toString());
    }
}

def String resolveVariables(String str, Message message) {

    def counter = message.getHeaders().get(PROPERTY_INVOCATION_COUNTER);
    if (counter == null) {
       counter = '0';
    }
	def result = str.replaceAll(~/\{COUNTER\}/, counter);
	
	return result;

}

def void processHeadersAndProperties(String prefix_with_vars, Message message) {
    Logger log = LoggerFactory.getLogger(this.getClass());
    try {
        def prefix = resolveVariables(prefix_with_vars, message);
        
        def StringBuffer sb_html = new StringBuffer();
        def StringBuffer sb_text = new StringBuffer();
        def map = message.getHeaders();
        
        def enable = false;
        if ('YES'.equalsIgnoreCase(LOG_HEADERS)) {
            enable = true;
        }
        
        if (enable) {
            map = excludeEntries(map, EXCLUDE_HEADERS);
            dumpProperties_HTML("Headers", map, sb_html);
            dumpProperties_TEXT("Headers", map, sb_text);
        }
        
        
        map = message.getProperties();
        
        enable = false;
        if ('YES'.equalsIgnoreCase(LOG_PROPERTIES)) {
            enable = true;
        }

        if (enable) {
            map = excludeEntries(map, EXCLUDE_PROPERTIES);
            dumpProperties_HTML("Properties", map, sb_html);
            dumpProperties_TEXT("Properties", map, sb_text);
        }
        
        
        enable = false;
        if ('YES'.equalsIgnoreCase(LOG_EXCEPTION)) {
            enable = true;
        }
        
        if (enable) {
            
        def ex = map.get("CamelExceptionCaught");
        if (ex!=null) {
            
            def exmap = new HashMap();
            exmap.put("exception",ex);
            exmap.put("getCanonicalName",ex.getClass().getCanonicalName());
            exmap.put("getMessage",ex.getMessage());
            
            StringWriter swe = new StringWriter();
            ex.printStackTrace(new PrintWriter(swe));
            exmap.put("stacktrace",swe.toString());
            
            if (ex.getClass().getCanonicalName().equals("org.apache.camel.component.ahc.AhcOperationFailedException")) {
                exmap.put("responseBody",org.apache.commons.lang.StringEscapeUtils.escapeXml(ex.getResponseBody()));
                exmap.put("getStatusText",ex.getStatusText());
                exmap.put("getStatusCode",ex.getStatusCode());
            }
            
            if (ex instanceof org.apache.cxf.interceptor.Fault) {
                exmap.put("getDetail",org.apache.commons.lang.StringEscapeUtils.escapeXml(ex.getDetail()));
                exmap.put("getFaultCode",ex.getFaultCode());
                exmap.put("getMessage",ex.getMessage());
                exmap.put("getStatusCode",""+ex.getStatusCode());
                exmap.put("hasDetails",""+ex.hasDetails());
                
                //message.getHeaders().put("SoapFaultMessage", ex.getMessage());
                exmap.put("getCause",""+ex.getCause());
                
                def cause_message = ex.getCause().getMessage();
                if (ex.getCause() instanceof org.apache.cxf.transport.http.HTTPException) {
                    cause_message = ex.getCause().getResponseMessage();
                }
                exmap.put("getCause.getResponseMessage",""+cause_message);
                
                message.getHeaders().put("SoapFaultMessage", ex.getMessage() +": "+ ex.getCause().getResponseMessage());
                
            }
            
            
            dumpProperties_HTML("property.CamelExceptionCaught", exmap, sb_html);
            dumpProperties_TEXT("property.CamelExceptionCaught", exmap, sb_text);
        }
            
        }

        
        enable = false;
        if ('YES'.equalsIgnoreCase(LOG_BODY_INFO)) {
            enable = true;
        }
        
        if (enable) {

            def body_test = message.getBody();
            def bodymap = new HashMap();
            
            bodymap.put("Body",body_test);
            
            if (body_test!=null) {
                bodymap.put("CanonicalClassName",body_test.getClass().getCanonicalName());
            }
            
            
            
            dumpProperties_HTML("Body", bodymap, sb_html);
            dumpProperties_TEXT("Body", bodymap, sb_text);
            

        }
        
        
        enable = false;
        if ('YES'.equalsIgnoreCase(LOG_OTHER)) {
            enable = true;
        }
        
        if (enable) {
       
            def othermap = new HashMap();
            def currentDate = new Date();
            def timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").format(currentDate);
            othermap.put("timestamp",timestamp);
            
            def lasttimestamp = message.getHeaders().get(PROPERTY_LAST_TIMESTAMP);
            if (lasttimestamp!=null) {
                 long diff = currentDate.getTime() - lasttimestamp.getTime();
                 othermap.put("last_timestamp_diff",diff);
            }
            
            dumpProperties_HTML("Others", othermap, sb_html);
            dumpProperties_TEXT("Others", othermap, sb_text);
            
            
            
            message.setHeader(PROPERTY_LAST_TIMESTAMP, currentDate);
            message.setHeader(PROPERTY_LAST_TIMESTAMP+"_str", timestamp);
            
        }
        
        
        
        def props = message.getProperties();
        def property_ENABLE_MPL_LOGGING = props.get("ENABLE_MPL_LOGGING");
        def property_ENABLE_FILE_LOGGING = props.get("ENABLE_FILE_LOGGING");
        
        def mpl_enabled = false;
        if ("ALWAYS".equalsIgnoreCase(MPL_LOGGING_MODE)) {
            mpl_enabled = true;
        } else if ("YES".equalsIgnoreCase(MPL_LOGGING_MODE)) {
            mpl_enabled = true;
        } else ("PROPERTY".equalsIgnoreCase(MPL_LOGGING_MODE)) {
            if ("TRUE".equalsIgnoreCase(property_ENABLE_MPL_LOGGING)) {
                mpl_enabled = true;
            }
        }
        
        
        def file_enabled = false;
        if ("ALWAYS".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            file_enabled = true;
        } else if ("YES".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            mpl_enabled = true;
        } else ("PROPERTY".equalsIgnoreCase(FILE_LOGGING_MODE)) {
            if ("TRUE".equalsIgnoreCase(property_ENABLE_FILE_LOGGING)) {
                file_enabled = true;
            }
        }
        
        
        if (mpl_enabled) {
            def messageLog = messageLogFactory.getMessageLog(message);
            def mpl_prefix = prefix;
            if (prefix.startsWith(IFLOW_NAME+'_')) {
               mpl_prefix = prefix.substring(IFLOW_NAME.length()+1);
            }
            messageLog.addAttachmentAsString(mpl_prefix, sb_text.toString(), "text/plain");
        }
        
        if (file_enabled) {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            def task = {c -> pool.submit( c as Callable)}
            task{saveFile(""+prefix+".html", sb_html.toString().getBytes())};
        }
        
    } catch (Exception ex01) {
        log.error("cannot save headers and properties",ex01)
        StringWriter sw = new StringWriter();
        ex01.printStackTrace(new PrintWriter(sw));
        log.info(sw.toString());
    }
    
}


public void saveFile(String fileName, byte[] bytes) {
    try {
        def String METVIEWER_FOLDER = "metviewer";
        java.nio.file.Path path = Paths.get(METVIEWER_FOLDER+"/"+fileName);
        path.toFile().delete();
        path.getParent().toFile().mkdir();
        if (bytes!=null) {
            Files.write(path, bytes, StandardOpenOption.CREATE);
        } else {
            Files.write(path, "".getBytes(), StandardOpenOption.CREATE);
        }
    } catch (Exception ex) {
        System.out.println("saveFile.exception: filename:"+fileName+" ex:"+ex);
        throw new RuntimeException(ex);
    }
}




public void dumpProperties(String title, Map<String, Object> map, StringBuffer sb) {
    sb.append(title+"\n");
    for (String key : map.keySet()) {
        sb.append(key+"\t"+map.get(key)+"\n");
    }
}

public void dumpProperties_HTML(String title, Map<String, Object> map, StringBuffer sb) {
    sb.append("<h1>"+title+"</h1><br>\n");
    sb.append("<table>\n");
    for (String key : map.keySet()) {
        sb.append("<tr>\n");
        sb.append("<td>"+key+"</td><td>"+map.get(key)+"</td>\n");
        sb.append("</tr>\n");
    }
    sb.append("</table>\n");
}


public void dumpProperties_TEXT(String title, Map<String, Object> map, StringBuffer sb) {
    sb.append(title+"\n");
    for (String key : map.keySet()) {
        sb.append(String.format(" %-40s: %-40s\n",key, map.get(key)));
    }
    sb.append("\n");
}
