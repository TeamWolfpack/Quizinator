package com.seniordesign.wolfpack.quizinator.database;

/**
 * An interface which represents necessary methods to share an object
 */
interface Shareable<T> {
    boolean toJsonFile(String filePath);
    T fromJson(String json);
    T fromJsonFile(String filePath);
}
