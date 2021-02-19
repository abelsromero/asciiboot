package org.asciidoctor.it.springboot;

import lombok.SneakyThrows;
import org.asciidoctor.jruby.GlobDirectoryWalker;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
        String converted = asciidoctorService.convert(new String(decodedContent));

        String encodedConvertedContent = Base64.getEncoder().encodeToString(converted.getBytes(StandardCharsets.UTF_8));
        return new ConvertedResource(encodedConvertedContent, MediaType.TEXT_HTML_VALUE);
    }

    @GetMapping("/all")
    public String convertAll() {
        long start = System.currentTimeMillis();

        convertAllInPath("**/*.adoc");
        // convertAllInPath("/**/*.adoc");

        return "time: " + (System.currentTimeMillis() - start) / 1000;
    }

    private void convertAllInPath(String path) {
        List<File> files = new GlobDirectoryWalker(path).scan();
        System.out.println("Found files: " + files.size() + " in " + new File(".").getTotalSpace());
        files.forEach(file -> {
            System.out.println("Converting: " + file.getAbsolutePath());
            String content = readFile(file);
            asciidoctorService.convert(content);
        });
    }

    @SneakyThrows
    private String readFile(File file) {
        return Files.readString(file.toPath());
    }

}
