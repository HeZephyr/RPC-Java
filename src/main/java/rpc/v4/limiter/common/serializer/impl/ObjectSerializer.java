package rpc.v4.limiter.common.serializer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.limiter.common.serializer.Serializer;

import java.io.*;

public class ObjectSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(ObjectSerializer.class);

    // Serialize the object into a byte array using ObjectOutputStream
    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush(); // Ensure all data is written
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Serialization failed for object: {}", object.getClass().getName(), e);
            return null;
        }
    }

    // Deserialize the byte array into an object
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Deserialization failed. Message type: {}", messageType, e);
            return null;
        }
    }

    @Override
    public int getType() {
        return 0; // Identifier for Java default serialization
    }
}