package com.revolut.denis.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.revolut.denis.exception.Reason;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private final boolean success;

    private final T entity;

    private final Reason reason;

    private Response(boolean success, T entity, Reason reason) {
        this.success = success;
        this.entity = entity;
        this.reason = reason;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getEntity() {
        return entity;
    }

    public Reason getReason() {
        return reason;
    }

    public static <E> Response<E> success(E entity) {
        return new Response<>(true, entity, null);
    }

    public static Response failed(Reason reason) {
        return new Response<>(false, null, reason);
    }

}
