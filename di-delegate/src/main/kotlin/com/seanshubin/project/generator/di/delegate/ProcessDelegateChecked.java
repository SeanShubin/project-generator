package com.seanshubin.project.generator.di.delegate;

import com.seanshubin.project.generator.di.contract.ProcessContractChecked;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

//
// This file was imported from: ../kotlin-reusable
// Module: di-delegate
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class ProcessDelegateChecked implements ProcessContractChecked {
    private final Process process;

    public ProcessDelegateChecked(Process process) {
        this.process = process;
    }

    @Override
    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return process.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return process.waitFor();
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        return process.waitFor(timeout, unit);
    }

    @Override
    public int exitValue() {
        return process.exitValue();
    }

    @Override
    public void destroy() {
        process.destroy();
    }

    @Override
    public ProcessContractChecked destroyForcibly() {
        return new ProcessDelegateChecked(process.destroyForcibly());
    }

    @Override
    public boolean isAlive() {
        return process.isAlive();
    }
}
