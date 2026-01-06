/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

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
