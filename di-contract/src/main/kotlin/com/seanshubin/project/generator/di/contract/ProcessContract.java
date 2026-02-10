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

public interface ProcessContract {
    OutputStream getOutputStream();

    InputStream getInputStream();

    InputStream getErrorStream();

    int waitFor();

    boolean waitFor(long timeout, TimeUnit unit);

    int exitValue();

    void destroy();

    ProcessContract destroyForcibly();

    boolean isAlive();
}
