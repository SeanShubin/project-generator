package com.seanshubin.project.generator.di.contract;

import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

//
// This file was imported from: /Users/seashubi/github.com/SeanShubin/kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface FilesContractChecked {
    InputStream newInputStream(Path path, OpenOption... options) throws IOException;

    OutputStream newOutputStream(Path path, OpenOption... options) throws IOException;

    SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException;

    SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir) throws IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir, String glob) throws IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException;

    Path createFile(Path path, FileAttribute<?>... attrs) throws IOException;

    Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException;

    Path createDirectories(Path dir, FileAttribute<?>... attrs) throws IOException;

    Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IOException;

    Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IOException;

    Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException;

    Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IOException;

    Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException;

    Path createLink(Path link, Path existing) throws IOException;

    void delete(Path path) throws IOException;

    boolean deleteIfExists(Path path) throws IOException;

    Path copy(Path source, Path target, CopyOption... options) throws IOException;

    Path move(Path source, Path target, CopyOption... options) throws IOException;

    Path readSymbolicLink(Path link) throws IOException;

    FileStore getFileStore(Path path) throws IOException;

    boolean isSameFile(Path path, Path path2) throws IOException;

    boolean isHidden(Path path) throws IOException;

    String probeContentType(Path path) throws IOException;

    <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options);

    <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException;

    Path setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException;

    Object getAttribute(Path path, String attribute, LinkOption... options) throws IOException;

    Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException;

    Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) throws IOException;

    Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms) throws IOException;

    UserPrincipal getOwner(Path path, LinkOption... options) throws IOException;

    Path setOwner(Path path, UserPrincipal owner) throws IOException;

    boolean isSymbolicLink(Path path);

    boolean isDirectory(Path path, LinkOption... options);

    boolean isRegularFile(Path path, LinkOption... options);

    FileTime getLastModifiedTime(Path path, LinkOption... options) throws IOException;

    Path setLastModifiedTime(Path path, FileTime time) throws IOException;

    long size(Path path) throws IOException;

    boolean exists(Path path, LinkOption... options);

    boolean notExists(Path path, LinkOption... options);

    boolean isReadable(Path path);

    boolean isWritable(Path path);

    boolean isExecutable(Path path);

    Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor) throws IOException;

    Path walkFileTree(Path start, FileVisitor<? super Path> visitor) throws IOException;

    BufferedReader newBufferedReader(Path path, Charset cs) throws IOException;

    BufferedReader newBufferedReader(Path path) throws IOException;

    BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options) throws IOException;

    BufferedWriter newBufferedWriter(Path path, OpenOption... options) throws IOException;

    long copy(InputStream in, Path target, CopyOption... options) throws IOException;

    long copy(Path source, OutputStream out) throws IOException;

    byte[] readAllBytes(Path path) throws IOException;

    List<String> readAllLines(Path path, Charset cs) throws IOException;

    List<String> readAllLines(Path path) throws IOException;

    Path write(Path path, byte[] bytes, OpenOption... options) throws IOException;

    Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) throws IOException;

    Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IOException;

    Stream<Path> list(Path dir) throws IOException;

    Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) throws IOException;

    Stream<Path> walk(Path start, FileVisitOption... options) throws IOException;

    Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException;

    Stream<String> lines(Path path, Charset cs) throws IOException;

    Stream<String> lines(Path path) throws IOException;

    // java 11 starts here
    String readString(Path path) throws IOException;

    String readString(Path path, Charset cs) throws IOException;

    Path writeString(Path path, CharSequence csq, OpenOption... options) throws IOException;

    Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) throws IOException;

    // java 12 starts here
    long mismatch(Path path, Path path2) throws IOException;
}
