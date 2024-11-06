package rpc.v4.circuitbreaker.common.serializer;


import rpc.v4.circuitbreaker.common.serializer.impl.JsonSerializer;
import rpc.v4.circuitbreaker.common.serializer.impl.ObjectSerializer;
import rpc.v4.circuitbreaker.common.serializer.impl.ProtoBufSerializer;

public interface Serializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes, int messageType);
    int getType();
    static Serializer getSerializer(int type) {
        return switch (type) {
            case 0 -> new ObjectSerializer();
            case 1 -> new JsonSerializer();
            case 2 -> new ProtoBufSerializer();
            default -> null;
        };
    }
}
