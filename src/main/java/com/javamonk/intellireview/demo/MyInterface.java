package com.javamonk.intellireview.demo;

public interface MyInterface {

    default void say(){
        System.out.println("say some");
    }

}
