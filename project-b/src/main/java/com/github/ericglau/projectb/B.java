package com.github.ericglau.projectb;

import com.github.ericglau.projecta.A;

public class B {
    public static void main(String[] args) {
        System.out.println("The imported message is: " + A.HELLO);
    }
}