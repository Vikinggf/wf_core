package com.wf.core.email;

import org.springframework.core.io.FileSystemResource;

import java.io.File;

public class AttachMent {
    private String name;
    private FileSystemResource file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileSystemResource getFile() {
        return file;
    }

    public void setFile(FileSystemResource file) {
        this.file = file;
    }

    public static AttachMent makeInstance(String name, String path) {
        AttachMent attachMent = new AttachMent();
        attachMent.setName(name);
        FileSystemResource file = new FileSystemResource(new File(path));
        attachMent.setFile(file);

        if (null == name || "".equals(name)) {
            attachMent.setName(file.getFilename());
        }
        return attachMent;
    }

    public static AttachMent makeInstance(String name, File fileSource) {
        AttachMent attachMent = new AttachMent();
        attachMent.setName(name);
        FileSystemResource file = new FileSystemResource(fileSource);
        attachMent.setFile(file);

        if (null == name || "".equals(name)) {
            attachMent.setName(file.getFilename());
        }

        return attachMent;
    }
}
