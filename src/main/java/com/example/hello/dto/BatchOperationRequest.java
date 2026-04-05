package com.example.hello.dto;

import java.util.List;

/**
 * 批量操作请求
 */
public class BatchOperationRequest {
    
    private List<Long> ids;

    public BatchOperationRequest() {
    }

    public BatchOperationRequest(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "BatchOperationRequest{" +
                "ids=" + ids +
                '}';
    }
}

