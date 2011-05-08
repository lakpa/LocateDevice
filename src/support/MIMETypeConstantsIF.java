package support;

/**
 * @author Nazmul Idris
 * @version 1.0
 * 
 * ConstantsIF contains various constants used in the clienthelper and EmailServlet impl of the mail
 * service. 
 * 
 * */
public interface MIMETypeConstantsIF {

//
// Constants
//
public static String SEND_EMAIL = "send_email";
public static String SEND_EMAIL_WITH_ATTACHMENT = "send_email_with_attachment";
public static String MESSAGE_SENT_OK = "message_sent_ok";

/** this is the key of the SMTP host stored in web.xml */
public static final String MAIL_HOST = "mail.host";
public static final String MAIL_PORT = "mail.port";
public static final String MAIL_USER = "mail.user";
public static final String MAIL_PSWD = "mail.pswd";

public static String PLAIN_TEXT_TYPE = "text/plain";
public static String HTML_TEXT_TYPE = "text/html";
public static String CSS_TYPE = "text/css";
public static String XML_TYPE = "text/xml";
public static String ICS_TYPE = "text/calendar";

public static String GIF_TYPE = "image/gif";
public static String PNG_TYPE = "image/x-png";
public static String JPEG_TYPE = "image/jpeg";
public static String TIFF_TYPE = "image/tiff";
public static String WINDOWS_BMP_TYPE = "image/x-ms-bmp";

public static String MP3_AUDIO_TYPE = "audio/mpeg";

public static String MPEG_VIDEO_TYPE = "video/mpeg";

public static String PDF_TYPE = "application/pdf";
public static String RTF_TYPE = "application/rtf";
public static String MSWORD_TYPE = "application/msword";
public static String ZIP_TYPE = "application/zip";
public static String BINARY_TYPE = "application/octet-stream";

}
