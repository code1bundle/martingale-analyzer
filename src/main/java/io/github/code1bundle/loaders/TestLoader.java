package io.github.code1bundle.loaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.code1bundle.loaders.dto.TestCase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestLoader {

    public List<TestCase> loadTestCasesFromJson(String filePath) throws IOException {
        try(
                InputStream in = new FileInputStream(filePath)
        ){
            return new ObjectMapper().readValue(in, new TypeReference<>(){});
        }
    }

    public List<TestCase> loadTestCasesFromCSV(String filePath) throws IOException {
        try (
                MappingIterator<TestCase> iterator = new CsvMapper().readerFor(TestCase.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(new File(filePath))
        ){
            return iterator.readAll();
        }
    }
}
