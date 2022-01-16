package com.marko19907.gitCleaner.model;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The class contains the git folder search algorithm.
 */
public class GitFolderSearch {

    /**
     * Default no-arg constructor.
     */
    public GitFolderSearch() {
        // Empty constructor
    }

    /**
     * Returns a Set of all folders that contain a .git folder inside recursively.
     * @throws IllegalArgumentException If the given path is null
     */
    public Set<File> getGitFolders(String givenPath) {
        // Guard condition
        if (givenPath == null) {
            throw new IllegalArgumentException("The given path can not be null!");
        }

        File file = new File(givenPath);

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            assert files != null;
            return this.recurse(files, 0, 0, new HashSet<>());
        }

        return Collections.emptySet();
    }

    /**
     * A step in the recursive algorithm.
     */
    private Set<File> recurse(File[] files, int index, final int level, Set<File> set) {
        if (index == files.length) {
            return set;
        }

        if (files[index].isDirectory()) {
            if (files[index].getName().equals(".git")) {
                set.add(files[index].getParentFile());
            }
            else {
                this.recurse(files[index].listFiles(), 0, level + 1, set);
            }
        }

        return this.recurse(files, ++index, level, set);
    }

    // -------------
    // Test methods
    // -------------

    /**
     * Prints out the found git folders in the given path, for testing only.
     * @param path The given path
     */
    private void verify(String path) {
        Set<File> files = this.getGitFolders(path);
        files.forEach(file -> {
            if (file != null && file.exists()) {
                System.out.println(file.getAbsolutePath());
            }
        });
    }

    public static void main(String[] args) {
        new GitFolderSearch().verify(".");
    }
}
