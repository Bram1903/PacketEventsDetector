package com.deathmotion.packeteventsdetector.models;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
public class ScannableFile {
    private final String name;
    private final ClassLoader classLoader;
    private final File file;

    @Setter
    private List<String> classNames;

    public ScannableFile(String name, ClassLoader classLoader, File file) {
        this.name = name;
        this.classLoader = classLoader;
        this.file = file;
    }
}
