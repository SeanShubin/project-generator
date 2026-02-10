package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.ProcessContract;

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

public class ProcessUnsupportedOperation implements ProcessContract {
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
    public int waitFor() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) {
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
    public ProcessContract destroyForcibly() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isAlive() {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
