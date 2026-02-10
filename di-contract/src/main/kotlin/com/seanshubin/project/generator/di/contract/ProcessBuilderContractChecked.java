package com.seanshubin.project.generator.di.contract;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//
// This file was imported from: ../kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface ProcessBuilderContractChecked {
    ProcessBuilderContractChecked command(List<String> command);

    ProcessBuilderContractChecked command(String... command);

    List<String> command();

    ProcessBuilderContractChecked directory(File directory);

    File directory();

    Map<String, String> environment();

    ProcessBuilderContractChecked redirectErrorStream(boolean redirectErrorStream);

    boolean redirectErrorStream();

    ProcessContractChecked start() throws IOException;
}
