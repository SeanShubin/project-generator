package com.seanshubin.project.generator.di.contract;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.util.Properties;
import java.util.ResourceBundle;

//
// This file was imported from: /Users/seashubi/github.com/SeanShubin/kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface SystemContract {
    void setIn(InputStream in);

    void setOut(PrintStream out);

    void setErr(PrintStream err);

    Console console();

    Channel inheritedChannel() throws IOException;

    long currentTimeMillis();

    long nanoTime();

    void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);

    int identityHashCode(Object x);

    Properties getProperties();

    String lineSeparator();

    void setProperties(Properties props);

    String getProperty(String key);

    String getProperty(String key, String def);

    String setProperty(String key, String value);

    String clearProperty(String key);

    String getenv(String name);

    java.util.Map<String, String> getenv();

    void exit(int status);

    void gc();

    void load(String filename);

    void loadLibrary(String libname);

    String mapLibraryName(String libname);

    // java 9 starts here
    System.Logger getLogger(String name);

    System.Logger getLogger(String name, ResourceBundle bundle);
}

