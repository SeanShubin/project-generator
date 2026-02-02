package com.seanshubin.project.generator.di.contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
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
// This file was imported from: ../kotlin-reusable
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface FilesContract {
    InputStream newInputStream(Path path, OpenOption... options);

    OutputStream newOutputStream(Path path, OpenOption... options);

    SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs);

    SeekableByteChannel newByteChannel(Path path, OpenOption... options);

    DirectoryStream<Path> newDirectoryStream(Path dir);

    DirectoryStream<Path> newDirectoryStream(Path dir, String glob);

    DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter);

    Path createFile(Path path, FileAttribute<?>... attrs);

    Path createDirectory(Path dir, FileAttribute<?>... attrs);

    Path createDirectories(Path dir, FileAttribute<?>... attrs);

    Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs);

    Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs);

    Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs);

    Path createTempDirectory(String prefix, FileAttribute<?>... attrs);

    Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs);

    Path createLink(Path link, Path existing);

    void delete(Path path);

    boolean deleteIfExists(Path path);

    Path copy(Path source, Path target, CopyOption... options);

    Path move(Path source, Path target, CopyOption... options);

    Path readSymbolicLink(Path link);

    FileStore getFileStore(Path path);

    boolean isSameFile(Path path, Path path2);

    boolean isHidden(Path path);

    String probeContentType(Path path);

    <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options);

    <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options);

    Path setAttribute(Path path, String attribute, Object value, LinkOption... options);

    Object getAttribute(Path path, String attribute, LinkOption... options);

    Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options);

    Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options);

    Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms);

    UserPrincipal getOwner(Path path, LinkOption... options);

    Path setOwner(Path path, UserPrincipal owner);

    boolean isSymbolicLink(Path path);

    boolean isDirectory(Path path, LinkOption... options);

    boolean isRegularFile(Path path, LinkOption... options);

    FileTime getLastModifiedTime(Path path, LinkOption... options);

    Path setLastModifiedTime(Path path, FileTime time);

    long size(Path path);

    boolean exists(Path path, LinkOption... options);

    boolean notExists(Path path, LinkOption... options);

    boolean isReadable(Path path);

    boolean isWritable(Path path);

    boolean isExecutable(Path path);

    Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor);

    Path walkFileTree(Path start, FileVisitor<? super Path> visitor);

    BufferedReader newBufferedReader(Path path, Charset cs);

    BufferedReader newBufferedReader(Path path);

    BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options);

    BufferedWriter newBufferedWriter(Path path, OpenOption... options);

    long copy(InputStream in, Path target, CopyOption... options);

    long copy(Path source, OutputStream out);

    byte[] readAllBytes(Path path);

    List<String> readAllLines(Path path, Charset cs);

    List<String> readAllLines(Path path);

    Path write(Path path, byte[] bytes, OpenOption... options);

    Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options);

    Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options);

    Stream<Path> list(Path dir);

    Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options);

    Stream<Path> walk(Path start, FileVisitOption... options);

    Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options);

    Stream<String> lines(Path path, Charset cs);

    Stream<String> lines(Path path);

    // java 11 starts here
    String readString(Path path);

    String readString(Path path, Charset cs);

    Path writeString(Path path, CharSequence csq, OpenOption... options);

    Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options);

    // java 12 starts here
    long mismatch(Path path, Path path2);
}

