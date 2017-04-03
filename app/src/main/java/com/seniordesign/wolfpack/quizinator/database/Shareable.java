package com.seniordesign.wolfpack.quizinator.database;

import java.io.File;

/**
 * An interface which represents necessary methods to share an object
 */
interface Shareable<T> {
    boolean toJsonFile(File dir, String fileName);
    T fromJson(String json);
    T fromJsonFilePath(String filePath);
}
