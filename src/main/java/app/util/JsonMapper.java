package app.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.function.Consumer;

public class JsonMapper {

    private final ObjectMapper mapper;

    public JsonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonMapper copy(Consumer<ObjectMapper> configurator) {
        ObjectMapper newMapper = mapper.copy();
        configurator.accept(newMapper);
        return new JsonMapper(newMapper);
    }

    public <T> T readValue(File src, Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(File src, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(src, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(content, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(Reader src, Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(Reader src, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(src, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(InputStream src, Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(src, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(byte[] src, Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) {
        try {
            return mapper.readValue(src, offset, len, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(src, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(src, offset, len, valueTypeRef);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public <T> T readValue(DataInput src, Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void writeValue(File resultFile, Object value) {
        try {
            mapper.writeValue(resultFile, value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void writeValue(OutputStream out, Object value) {
        try {
            mapper.writeValue(out, value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void writeValue(DataOutput out, Object value) {
        try {
            mapper.writeValue(out, value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void writeValue(Writer w, Object value) {
        try {
            mapper.writeValue(w, value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public byte[] writeValueAsBytes(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
