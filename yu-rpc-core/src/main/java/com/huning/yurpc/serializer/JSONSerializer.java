package com.huning.yurpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;

import java.io.IOException;
import java.security.spec.RSAPrivateCrtKeySpec;

public class JSONSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(data, clazz);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }


    private <T> T handleRequest(RpcRequest request, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();

        for(int i = 0; i < parameterTypes.length; i++){
            Class<?> parameterType = parameterTypes[i];
            if(!parameterType.isAssignableFrom(args[i].getClass())){
                byte[] argByte = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argByte, parameterType);
            }
        }
        return type.cast(request);
    }


    private <T> T handleResponse(RpcResponse response, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
        response.setData(OBJECT_MAPPER.readValue(dataBytes,type));
        return type.cast(response);
    }


}
