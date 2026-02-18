package com.javamonk.intellireview.demo;

public class MyClass implements MyInterface{

//   public void say(){
//        System.out.println("hello");
//    }

    public static void main(String[] args) {

        MyInterface m = new MyClass();
        m.say();

    }

}
