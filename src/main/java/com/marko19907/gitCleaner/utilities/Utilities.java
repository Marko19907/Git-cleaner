package com.marko19907.gitCleaner.utilities;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Objects;

public class Utilities {

    /**
     * Formats the given size in bytes to a human-readable format, binary unites (1 K = 1,024).
     * @param size The size in bytes to format
     */
    public static String formatSize(long size) {
        long absB = size == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(size);
        if (absB < 1024) {
            return size + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(size);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    /**
     * Returns the program's icon.
     */
    public static Image getIcon() {
        return new Image(Objects.requireNonNull(Utilities.class.getResourceAsStream("/icon.png")));
    }

    /**
     * Adds an icon to the title bar of the given Dialog.
     * @param dialog The dialog to set the icon to, not null
     */
    public static void addIconToDialog(Dialog<?> dialog) {
        if (dialog != null) {
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(Utilities.getIcon());
        }
    }

    public static void main(String[] args) {
        String result = formatSize(19751349);
        System.out.println(result);
    }
}
