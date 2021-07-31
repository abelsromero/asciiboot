package org.asciidoctor.it.springboot;

import lombok.Data;

@Data
public class SourceContent {

    /**
     * Base64 encoded
     */
    private String data;
    private Options options;
    
    @Data
    static class Options {
        private String backend;
    }

}
