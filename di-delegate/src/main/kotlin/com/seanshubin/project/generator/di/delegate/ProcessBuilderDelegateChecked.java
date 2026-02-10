package com.seanshubin.project.generator.di.delegate;

import com.seanshubin.project.generator.di.contract.ProcessBuilderContractChecked;
import com.seanshubin.project.generator.di.contract.ProcessContractChecked;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//
// This file was imported from: ../kotlin-reusable
// Module: di-delegate
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class ProcessBuilderDelegateChecked implements ProcessBuilderContractChecked {
    private final ProcessBuilder processBuilder;

    public ProcessBuilderDelegateChecked() {
        this.processBuilder = new ProcessBuilder();
    }

    public ProcessBuilderDelegateChecked(List<String> command) {
        this.processBuilder = new ProcessBuilder(command);
    }

    public ProcessBuilderDelegateChecked(String... command) {
        this.processBuilder = new ProcessBuilder(command);
    }

    private ProcessBuilderDelegateChecked(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    @Override
    public ProcessBuilderContractChecked command(List<String> command) {
        processBuilder.command(command);
        return this;
    }

    @Override
    public ProcessBuilderContractChecked command(String... command) {
        processBuilder.command(command);
        return this;
    }

    @Override
    public List<String> command() {
        return processBuilder.command();
    }

    @Override
    public ProcessBuilderContractChecked directory(File directory) {
        processBuilder.directory(directory);
        return this;
    }

    @Override
    public File directory() {
        return processBuilder.directory();
    }

    @Override
    public Map<String, String> environment() {
        return processBuilder.environment();
    }

    @Override
    public ProcessBuilderContractChecked redirectErrorStream(boolean redirectErrorStream) {
        processBuilder.redirectErrorStream(redirectErrorStream);
        return this;
    }

    @Override
    public boolean redirectErrorStream() {
        return processBuilder.redirectErrorStream();
    }

    @Override
    public ProcessContractChecked start() throws IOException {
        Process process = processBuilder.start();
        return new ProcessDelegateChecked(process);
    }
}
