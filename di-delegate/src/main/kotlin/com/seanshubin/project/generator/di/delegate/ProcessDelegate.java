package com.seanshubin.project.generator.di.delegate;

import com.seanshubin.project.generator.di.contract.ProcessContract;
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

public class ProcessDelegate implements ProcessContract {
    private final ProcessContractChecked delegate;

    public ProcessDelegate(ProcessContractChecked delegate) {
        this.delegate = delegate;
    }

    @Override
    public OutputStream getOutputStream() {
        return delegate.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return delegate.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return delegate.getErrorStream();
    }

    @Override
    public int waitFor() {
        try {
            return delegate.waitFor();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) {
        try {
            return delegate.waitFor(timeout, unit);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public int exitValue() {
        return delegate.exitValue();
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public ProcessContract destroyForcibly() {
        return new ProcessDelegate(delegate.destroyForcibly());
    }

    @Override
    public boolean isAlive() {
        return delegate.isAlive();
    }
}
