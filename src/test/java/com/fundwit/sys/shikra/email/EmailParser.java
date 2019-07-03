package com.fundwit.sys.shikra.email;

import com.fundwit.sys.shikra.email.linereader.LineReader;
import com.fundwit.sys.shikra.email.linereader.ReaderWrapperLineReader;
import com.fundwit.sys.shikra.email.linereader.StringListLineReader;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EmailParser {
    public static final String MIME_TYPE_MULTIPART = "multipart";

    public static final String MIME_SUBTYPE_MIXED = "mixed";
    public static final String MIME_SUBTYPE_RELATED = "related";
    public static final String MIME_SUBTYPE_ALTERNATIVE = "alternative";

    public static final String PARAMETER_NAME_BOUNDARY = "boundary";

    public static final String HEADER_NAME_SUBJECT = "SUBJECT";

    // 读取header，直到一个空行
    public MimeBodyPart resolve(InputStream inputStream) throws IOException, MessagingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        MimeBodyPart mimeBodyPart = this.parsePart(new ReaderWrapperLineReader(reader));
        return mimeBodyPart;
    }

    private MimeBodyPart parsePart(LineReader lineReader) throws IOException, MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();

        Map<String, String> partHeaders = this.readHeaders(lineReader);
        for(Map.Entry<String, String> kv: partHeaders.entrySet()) {
            bodyPart.addHeader(kv.getKey(), kv.getValue());
        }
        MimeType partMimeType = MimeType.valueOf(partHeaders.get(HttpHeaders.CONTENT_TYPE));

        if(MIME_TYPE_MULTIPART.equalsIgnoreCase(partMimeType.getType())) {
            MimeMultipart mimeMultipart = parseMultipartBody(partMimeType, lineReader);
            mimeMultipart.setParent(bodyPart);
            bodyPart.setContent(mimeMultipart, partMimeType.toString());
        }else if("text".equalsIgnoreCase(partMimeType.getType())) {
            String transferEncoding = partHeaders.get("Content-Transfer-Encoding");

            Charset charset = StandardCharsets.UTF_8;
            String charEncoding = partMimeType.getParameter("charset");
            if(StringUtils.hasText(charEncoding)) {
                charset = Charset.forName(charEncoding);
            }

            String content = "";
            if(!StringUtils.hasText(transferEncoding)) {
                String line = null;
                while ((line = lineReader.readLine()) != null) {
                    content += line;
                }
            }
            else if("base64".equalsIgnoreCase(transferEncoding)) {
                String base64Content = "";
                String line = null;
                while ((line = lineReader.readLine()) != null) {
                    base64Content += line;
                }
                base64Content = base64Content.trim();
                content = new String(Base64.getDecoder().decode(base64Content.getBytes(charset)), charset);
            } else {
                throw new RuntimeException("not support type " + partMimeType.toString());
            }

            bodyPart.setContent(content, partMimeType.toString());

        }else {
            throw new RuntimeException("not support type " + partMimeType.toString());
        }

        return bodyPart;
    }

    private MimeMultipart parseMultipartBody(MimeType mimeType, LineReader bodyLineReader) throws IOException, MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.setSubType(mimeType.getSubtype());

        String boundary = mimeType.getParameter(PARAMETER_NAME_BOUNDARY)
                .replaceAll("^\"", "").replaceAll("\"$", "");

        // mixed
        if(MIME_SUBTYPE_MIXED.equalsIgnoreCase(mimeType.getSubtype())
           || MIME_SUBTYPE_RELATED.equalsIgnoreCase(mimeType.getSubtype())) {
            // parse parts
            for(List<String> part : this.splitBodyByBoundary(boundary, bodyLineReader)) {
                MimeBodyPart bodyPart = this.parsePart(new StringListLineReader(part));
                mimeMultipart.addBodyPart(bodyPart);
            }
            return mimeMultipart;
        }
        // related, alternative, ...
        else{
            throw new RuntimeException("not supported body");
        }
    }

    private List<List<String>> splitBodyByBoundary(String boundary, LineReader lineReader) throws IOException {
        List<List<String>> parts = new ArrayList<>();
        List<String> currentPartLines = null;
        boolean inPart = false;

        // read parts
        String line = null;
        while ((line = lineReader.readLine())!=null) {
            if(!StringUtils.hasText(line) && !inPart) {
                continue;
            }
            if(line.contains(boundary)) {
                inPart = !inPart;
                if(!inPart) {
                    parts.add(currentPartLines);
                }else{
                    currentPartLines = new ArrayList<>();
                }
            }else{
                currentPartLines.add(line);
            }
        }

        if((parts.size() == 0 || parts.get(parts.size()-1) != currentPartLines)
                && currentPartLines != null) {
            parts.add(currentPartLines);
        }

        return parts;
    }

    private Map<String, String> readHeaders(LineReader reader) throws IOException {
        String line = null;
        // \n, \r, \r\n
        List<String> headerLines = new ArrayList<>();
        while ((line = reader.readLine())!=null) {
            if(!StringUtils.hasText(line)) {
                // header is end
                break;
            }else if(StringUtils.hasText(line.substring(0,1))){
                headerLines.add(line);
            }else{
                if(headerLines.size()>0) {
                    String mergedLine = headerLines.get(headerLines.size() - 1) + " " + line.trim();
                    headerLines.set(headerLines.size() - 1, mergedLine);
                }else{
                    throw new IllegalArgumentException("bad email body, unexpected whitespace char in the beginning line.");
                }
            }
        }

        Map<String, String> headers = new TreeMap<>();
        for(String headerLine: headerLines) {
            int idx = headerLine.indexOf(':');
            if(idx <= 0 || idx >= headerLine.length()) {
                throw new IllegalArgumentException("bad email body, illegal header "+ headerLine);
            }
            String key = headerLine.substring(0, idx);
            String value = headerLine.substring(idx+1).trim();
            headers.put(key, value);
        }

        return headers;
    }
}
