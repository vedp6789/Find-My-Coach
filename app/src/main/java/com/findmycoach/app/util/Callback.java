package com.findmycoach.app.util;

/**
 * Created by prem on 18/12/14.
 */
public interface Callback {

    public void successOperation(Object object, int statusCode, int calledApiValue);

    public void failureOperation(Object object, int statusCode, int calledApiValue);



}
