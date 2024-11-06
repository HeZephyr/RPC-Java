package rpc.v3.balancing.common.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import rpc.v3.balancing.common.message.RpcRequest;
import rpc.v3.balancing.common.message.RpcResponse;
import rpc.v3.balancing.common.serializer.Serializer;

public class JsonSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        // Serialize the object to a JSON byte array
        return JSON.toJSONBytes(object);
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object object = null;
        String str = new String(bytes);
        switch (messageType) {
            case 0 -> { // If message type is a request (RpcRequest)
                // Deserialize byte array into an RpcRequest object
                RpcRequest request = JSON.parseObject(bytes, RpcRequest.class, JSONReader.Feature.SupportClassForName);
                Object[] objects = new Object[request.getParameters().length];

                // Transform parameters to the target types specified in paramTypes
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramType = request.getParamTypes()[i];
                    // Check if the actual parameter type is compatible with paramType
                    if (!paramType.isAssignableFrom(request.getParameters()[i].getClass())) {
                        // If incompatible, serialize to JSON and deserialize to target type
                        objects[i] = JSON.parseObject(JSON.toJSONString(request.getParameters()[i]), paramType);
                    } else {
                        // If compatible, directly assign the parameter
                        objects[i] = request.getParameters()[i];
                    }
                }
                // Set the transformed parameters back into the request
                request.setParameters(objects);
                object = request;
            }
            case 1 -> { // If message type is a response (RpcResponse)
                // Deserialize byte array into an RpcResponse object
                RpcResponse response = JSON.parseObject(bytes, RpcResponse.class, JSONReader.Feature.SupportClassForName);
                // Get the target type of the response data
                Class<?> dataType = response.getDataType();

                // Check if the actual data type is compatible with dataType
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    // If incompatible, serialize to JSON and deserialize to target type
                    response.setData(JSON.parseObject(JSON.toJSONString(response.getData()), dataType));
                }
                object = response;
            }
            default -> throw new IllegalStateException("Unexpected value: " + messageType);
        }
        return object;
    }

    @Override
    public int getType() {
        return 1; // JSON serialization type identifier
    }
}