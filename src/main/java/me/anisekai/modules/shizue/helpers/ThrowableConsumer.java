package me.anisekai.modules.shizue.helpers;

public interface ThrowableConsumer<T> {

    void using(T t) throws Exception;

}
