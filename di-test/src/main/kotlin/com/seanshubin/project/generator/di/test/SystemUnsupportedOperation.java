package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.SystemContract;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

//
// This file was imported from: /Users/seashubi/github.com/SeanShubin/kotlin-reusable
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class SystemUnsupportedOperation implements SystemContract {
    @Override
    public void setIn(InputStream in) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void setOut(PrintStream out) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void setErr(PrintStream err) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Console console() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Channel inheritedChannel() throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long currentTimeMillis() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long nanoTime() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int identityHashCode(Object x) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Properties getProperties() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String lineSeparator() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void setProperties(Properties props) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String getProperty(String key, String def) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String setProperty(String key, String value) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String clearProperty(String key) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String getenv(String name) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Map<String, String> getenv() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void exit(int status) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void gc() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void load(String filename) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void loadLibrary(String libname) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String mapLibraryName(String libname) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public System.Logger getLogger(String name) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public System.Logger getLogger(String name, ResourceBundle bundle) {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
