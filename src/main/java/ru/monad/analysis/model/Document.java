package ru.monad.analysis.model;

import java.io.InputStream;
import java.util.Map;

public record Document(Long id, InputStream content, Map<String, ?> properties) {
}
