package com.seanshubin.project.generator.di.delegate;

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
// This file was imported from: ../kotlin-reusable
// Module: di-delegate
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class SystemDelegate implements SystemContract {
    private static SystemContract INSTANCE;

    public static SystemContract defaultInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemDelegate();
        }
        return INSTANCE;
    }

    @Override
    public void setIn(InputStream in) {
        System.setIn(in);
    }

    @Override
    public void setOut(PrintStream out) {
        System.setOut(out);
    }

    @Override
    public void setErr(PrintStream err) {
        System.setErr(err);
    }

    @Override
    public Console console() {
        return System.console();
    }

    @Override
    public Channel inheritedChannel() throws IOException {
        return System.inheritedChannel();
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }

    @Override
    public void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    @Override
    public int identityHashCode(Object x) {
        return System.identityHashCode(x);
    }

    @Override
    public Properties getProperties() {
        return System.getProperties();
    }

    @Override
    public String lineSeparator() {
        return System.lineSeparator();
    }

    @Override
    public void setProperties(Properties props) {
        System.setProperties(props);
    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    @Override
    public String setProperty(String key, String value) {
        return System.setProperty(key, value);
    }

    @Override
    public String clearProperty(String key) {
        return System.clearProperty(key);
    }

    @Override
    public String getenv(String name) {
        return System.getenv(name);
    }

    @Override
    public Map<String, String> getenv() {
        return System.getenv();
    }

    @Override
    public void exit(int status) {
        System.exit(status);
    }

    @Override
    public void gc() {
        System.gc();
    }

    @Override
    public void load(String filename) {
        System.load(filename);
    }

    @Override
    public void loadLibrary(String libname) {
        System.loadLibrary(libname);
    }

    @Override
    public String mapLibraryName(String libname) {
        return System.mapLibraryName(libname);
    }

    @Override
    public System.Logger getLogger(String name) {
        return System.getLogger(name);
    }

    @Override
    public System.Logger getLogger(String name, ResourceBundle bundle) {
        return System.getLogger(name, bundle);
    }
}
