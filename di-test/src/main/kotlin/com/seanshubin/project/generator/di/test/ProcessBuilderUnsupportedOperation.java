package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.ProcessBuilderContract;
import com.seanshubin.project.generator.di.contract.ProcessContract;

import java.io.File;
import java.util.List;
import java.util.Map;

//
// This file was imported from: ../kotlin-reusable
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class ProcessBuilderUnsupportedOperation implements ProcessBuilderContract {
    @Override
    public ProcessBuilderContract command(List<String> command) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract command(String... command) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> command() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract directory(File directory) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public File directory() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Map<String, String> environment() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessBuilderContract redirectErrorStream(boolean redirectErrorStream) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean redirectErrorStream() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public ProcessContract start() {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
