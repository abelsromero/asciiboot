package org.asciidoctor.it.springboot;

import org.asciidoctor.jruby.GlobDirectoryWalker;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;


/**
 * Simple Spring MVC Rest Controller to convert AsciiDoc sources to HTML.
 */
@RestController
public class ConverterController {

    private final AsciidoctorService asciidoctorService;

    public ConverterController(AsciidoctorService asciidoctorService) {
        this.asciidoctorService = asciidoctorService;
    }

    @PostMapping("/asciidoc")
    public ConvertedResource convert(@RequestBody SourceContent content) {
        byte[] decodedContent = Base64.getDecoder().decode(content.getData());
        byte[] converted = asciidoctorService.convert(new String(decodedContent), content.getOptions());

        String encodedConvertedContent = Base64.getEncoder().encodeToString(converted);
        return new ConvertedResource(encodedConvertedContent, contentType(content));
    }

    private String contentType(SourceContent content) {
        if (content == null || content.getOptions() == null || !StringUtils.hasText(content.getOptions().getBackend()))
            return MediaType.TEXT_HTML_VALUE;

        if (content.getOptions().getBackend().equalsIgnoreCase("pdf"))
            return MediaType.APPLICATION_PDF_VALUE;

        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @GetMapping("/all")
    public String convertAll() {
        long start = System.currentTimeMillis();

        convertAllInPath("**/*.adoc", new SourceContent.Options());
        // convertAllInPath("/**/*.adoc");

        return "time: " + (System.currentTimeMillis() - start) / 1000;
    }

    private void convertAllInPath(String path, SourceContent.Options options) {
        List<File> files = new GlobDirectoryWalker(path).scan();
        System.out.println("Found files: " + files.size() + " in " + new File(".").getTotalSpace());
        files.forEach(file -> {
            System.out.println("Converting: " + file.getAbsolutePath());
            String content = readFile(file);
            asciidoctorService.convert(content, options);
        });
    }

    private String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
