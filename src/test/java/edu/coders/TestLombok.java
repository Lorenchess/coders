package edu.coders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TestLombok {
    private final String name;

    public static void main(String[] args) {
        TestLombok obj = new TestLombok("Lombok Test");
        System.out.println("Name: " + obj.getName());
    }
}
