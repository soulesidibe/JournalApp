package com.soulesidibe.journalapp.model.data;

/**
 * Created on 6/27/18 at 12:43 PM
 * Project name : JournalApp
 */

public class Resource<T> {

    private ResourceState state;

    private T data;

    private String message;

    public Resource(ResourceState state, T data, String message) {
        this.state = state;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource onSuccess(T data) {
        return new Resource<T>(ResourceState.SUCCESS, data, null);
    }

    public static <T> Resource onError(String message) {
        return new Resource<T>(ResourceState.ERROR, null, message);
    }

    public static <T> Resource onLoading() {
        return new Resource<T>(ResourceState.LOADING, null, null);
    }

    public ResourceState getState() {
        return state;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public enum ResourceState {
        SUCCESS, LOADING, ERROR
    }
}
