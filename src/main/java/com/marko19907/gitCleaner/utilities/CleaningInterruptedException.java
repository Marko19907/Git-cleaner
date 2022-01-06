package com.marko19907.gitCleaner.utilities;

/**
 * A custom unchecked exception used to signal that the cleaning process was interrupted.
 */
public class CleaningInterruptedException extends RuntimeException {

    public CleaningInterruptedException(String message) {
        super(message);
    }
}
