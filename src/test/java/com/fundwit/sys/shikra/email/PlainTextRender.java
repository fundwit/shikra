package com.fundwit.sys.shikra.email;

import org.springframework.util.MimeType;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;

public class PlainTextRender {
    public String render(Part part) throws MessagingException, IOException {
        if(part == null) {
            return "";
        }

        if(part instanceof Multipart) {
            return this.renderMultipart((Multipart)part);
        }else{
            MimeType type = MimeType.valueOf(part.getDataHandler().getContentType());
            Object content = part.getDataHandler().getContent();

            if("multipart".equalsIgnoreCase(type.getType()) && content instanceof Multipart){
                return this.renderMultipart((Multipart)content);
            }
            else if("text".equalsIgnoreCase(type.getType())) {
                return content.toString();
            }else{
                return "[data("+part.getContentType()+")]";
            }
        }
    }

    public String renderMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i< multipart.getCount(); i++) {
            stringBuilder.append(this.render(multipart.getBodyPart(i)));
        }
        return stringBuilder.toString();
    }
}
