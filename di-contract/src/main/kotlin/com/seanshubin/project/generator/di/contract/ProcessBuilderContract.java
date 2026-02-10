package com.seanshubin.project.generator.di.contract;

import java.io.File;
import java.util.List;
import java.util.Map;

//
// This file was imported from: ../kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface ProcessBuilderContract {
    ProcessBuilderContract command(List<String> command);

    ProcessBuilderContract command(String... command);

    List<String> command();

    ProcessBuilderContract directory(File directory);

    File directory();

    Map<String, String> environment();

    ProcessBuilderContract redirectErrorStream(boolean redirectErrorStream);

    boolean redirectErrorStream();

    ProcessContract start();
}
