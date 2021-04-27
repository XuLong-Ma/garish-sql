package org.dregs.garish.sql.utils;

import java.util.function.Consumer;

public class Logs {

    private static Consumer<Exception> consumer;

    public static void error(Exception e){
        if(null != consumer) consumer.accept(e);
        else e.printStackTrace();
    }

    public static void setConsumer(Consumer<Exception> consumer) {
        Logs.consumer = consumer;
    }
}
