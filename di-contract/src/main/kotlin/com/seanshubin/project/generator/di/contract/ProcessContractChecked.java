package com.seanshubin.project.generator.di.contract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

//
// This file was imported from: ../kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface ProcessContractChecked {
    OutputStream getOutputStream();

    InputStream getInputStream();

    InputStream getErrorStream();

    int waitFor() throws InterruptedException;

    boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException;

    int exitValue();

    void destroy();

    ProcessContractChecked destroyForcibly();

    boolean isAlive();
}
