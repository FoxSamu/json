package dev.runefox.json;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class ObjectCodecTask extends DefaultTask {
    private File out;
    private String pkg;
    private int maxParams;

    public void maxParams(int maxParams) {
        this.maxParams = maxParams;
    }

    public void setMaxParams(int maxParams) {
        this.maxParams = maxParams;
    }

    @Input
    public int getMaxParams() {
        return maxParams;
    }

    public void pkg(String pkg) {
        this.pkg = pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    @Input
    public String getPkg() {
        return pkg;
    }

    public void out(File out) {
        this.out = out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    @InputDirectory
    public File getOut() {
        return out;
    }

    @TaskAction
    public void doTask() throws IOException {
        new ObjectCodecGenerator(out, pkg, maxParams).run();
    }
}
