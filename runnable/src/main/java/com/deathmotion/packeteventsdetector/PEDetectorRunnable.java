package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PEDetectorRunnable extends PEDetectorPlatform<PEDetectorRunnable> {

    public static void main(String[] args) {
        PEDetectorRunnable detector = new PEDetectorRunnable();
        detector.commonOnEnable();
    }

    @Override
    public PEDetectorRunnable getPlatform() {
        return this;
    }

    @Override
    public List<ScannableFile> getFiles() {
        File currentDir = new File(".");
        File[] filesArray = currentDir.listFiles();

        if (filesArray == null) return Collections.emptyList();

        return Arrays.stream(filesArray)
                .filter(file -> file.isFile() && file.getName().endsWith(".jar"))
                .map(file -> new ScannableFile(file.getName(), getClass().getClassLoader(), file))
                .collect(Collectors.toList());
    }
}

