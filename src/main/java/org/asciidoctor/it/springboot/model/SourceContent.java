package org.asciidoctor.it.springboot.model;


/**
 * @param data Base64 encoded
 */
public record SourceContent(String data, Options options) {

    public static class Options {

        private final String backend;

        public Options() {
            this.backend = null;
        }

        public Options(String backend) {
            this.backend = backend;
        }

        public String getBackend() {
            return backend;
        }
    }
}
