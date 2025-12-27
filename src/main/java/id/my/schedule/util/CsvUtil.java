package id.my.schedule.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class CsvUtil {

    public List<CSVRecord> readRecords(MultipartFile uploadFile) throws IOException {

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setTrim(true)
                .setIgnoreHeaderCase(true)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = format.parse(reader)) {

            // Menjalankan .getRecords() di sini yang aman
            return parser.getRecords();

        } catch (IOException e) {
            // Biarkan Service Layer yang memutuskan ResponseStatusException mana yang harus dilempar
            throw new IOException("Gagal membaca file: " + e.getMessage(), e);
        }
    }
}
