package org.asciidoctor.it.springboot;


public class SourceContent {

    /**
     * Base64 encoded
     */
    private final String data;
    private final Options options;

    public SourceContent(String data, Options options) {
        this.data = data;
        this.options = options;
    }

    public String getData() {
        return data;
    }

    public Options getOptions() {
        return options;
    }

    static class Options {
        private final String backend;

        Options() {
            this.backend = null;
        }

        Options(String backend) {
            this.backend = backend;
        }

        public String getBackend() {
            return backend;
        }
    }
}
