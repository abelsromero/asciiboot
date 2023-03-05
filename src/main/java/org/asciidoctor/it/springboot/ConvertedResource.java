package org.asciidoctor.it.springboot;


public class ConvertedResource {

    private final String content;
    private final String contentType;

    public ConvertedResource(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
