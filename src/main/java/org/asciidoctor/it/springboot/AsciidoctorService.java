package org.asciidoctor.it.springboot;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.it.springboot.model.SourceContent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

@Component
public class AsciidoctorService {

    private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();


    public byte[] convert(String source, SourceContent.Options options) {

        if (options == null || !"pdf".equals(options.getBackend())) {
            return asciidoctor.convert(source, buildOptions(options)).getBytes(StandardCharsets.UTF_8);
        }

        final File tempOutput = extracted();

        asciidoctor.convert(source, buildOptions(options, builder -> {
            builder.mkDirs(true);
            builder.toFile(tempOutput);
        }));
        return readString(tempOutput);
    }

    private byte[] readString(File tempOutput) {
        try {
            return Files.readAllBytes(tempOutput.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Options buildOptions(SourceContent.Options options) {
        return Options.builder()
            .backend(options != null && StringUtils.hasText(options.getBackend()) ? options.getBackend() : "html5")
            .safe(SafeMode.UNSAFE)
            .standalone(true)
            .toFile(false)
            .build();
    }

    private Options buildOptions(SourceContent.Options options, Consumer<OptionsBuilder> optionsCustomizer) {
        final OptionsBuilder optionsBuilder = Options.builder()
            .backend(StringUtils.hasText(options.getBackend()) ? options.getBackend() : "html5")
            .safe(SafeMode.UNSAFE)
            .standalone(true)
            .toFile(false);
        optionsCustomizer.accept(optionsBuilder);
        return optionsBuilder
            .build();
    }

    private File extracted() {
        try {
            return File.createTempFile("asciidoctor-pdf-", ".pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
