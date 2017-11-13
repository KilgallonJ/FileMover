/**
 * (c) Midland Software Limited 2017
 * Name     : FileExtensionFilter.java
 * Author   : kilgallonj
 * Date     : 13 Nov 2017
 */
package uk.co.mhr.filemover.app;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter used by the app to filter out files based on their extension.
 */
public class FileExtensionFilter implements FileFilter {

    private String extension;

    public FileExtensionFilter(final String ext) {
        this.extension = ext == null ? "" : ext;
    }

    @Override
    public boolean accept(final File pathname) {
        if (pathname.isFile()) {
            return pathname.getName().endsWith(extension);
        }

        return false;
    }
}
