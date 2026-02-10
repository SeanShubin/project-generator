package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.ProcessContractChecked;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

//
// This file was imported from: ../kotlin-reusable
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class ProcessUnsupportedOperationChecked implements ProcessContractChecked {
    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public InputStream getErrorStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int waitFor() throws InterruptedException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int exitValue() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessContractChecked destroyForcibly() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isAlive() {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
