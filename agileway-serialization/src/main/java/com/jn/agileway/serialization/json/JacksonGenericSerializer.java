package com.jn.agileway.serialization.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.SerializationException;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;

public class JacksonGenericSerializer<T> implements GenericSerializer<T> {
    private final ObjectMapper mapper;

    public JacksonGenericSerializer() {
        this((String) null);
    }

    public JacksonGenericSerializer(@Nullable String classPropertyTypeName) {
        this(new ObjectMapper());
        if (Strings.isNotBlank(classPropertyTypeName)) {
            this.mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, classPropertyTypeName);
        } else {
            this.mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

    }

    public JacksonGenericSerializer(ObjectMapper mapper) {
        Preconditions.checkNotNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;
    }


    public byte[] serialize(@Nullable T source) throws SerializationException {
        if (source == null) {
            return null;
        } else {
            try {
                return this.mapper.writeValueAsBytes(source);
            } catch (JsonProcessingException var3) {
                throw new SerializationException("Could not write JSON: " + var3.getMessage(), var3);
            }
        }
    }

    public T deserialize(@Nullable byte[] source) throws SerializationException {
        return (T) this.deserialize(source, (Class<T>) Object.class);
    }

    @Nullable
    public T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {
        Preconditions.checkNotNull(type, "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
        if (Emptys.isEmpty(source)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(source, type);
            } catch (Exception ex) {
                throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
    }


    @Override
    public boolean canSerialize(Class<?> type) {
        return true;
    }

    @Override
    public Class<?> getTargetType() {
        return Object.class;
    }
}
