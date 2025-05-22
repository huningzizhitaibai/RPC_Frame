package com.huning.yurpc.fault.retry;

import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {
    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
