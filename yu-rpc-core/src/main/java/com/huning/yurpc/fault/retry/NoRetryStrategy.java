package com.huning.yurpc.fault.retry;

import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;

import java.util.concurrent.Callable;

public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
