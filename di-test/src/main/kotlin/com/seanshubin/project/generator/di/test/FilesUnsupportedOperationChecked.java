package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.FilesContractChecked;

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
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class FilesUnsupportedOperationChecked implements FilesContractChecked {
    @Override
    public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createLink(Path link, Path existing) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path readSymbolicLink(Path link) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String probeContentType(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isSymbolicLink(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isDirectory(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isRegularFile(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public FileTime getLastModifiedTime(Path path, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long size(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean exists(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean notExists(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isReadable(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isWritable(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isExecutable(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path walkFileTree(Path start, FileVisitor<? super Path> visitor) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedReader newBufferedReader(Path path, Charset cs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedReader newBufferedReader(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long copy(InputStream in, Path target, CopyOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long copy(Path source, OutputStream out) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> readAllLines(Path path, Charset cs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> readAllLines(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> list(Path dir) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> walk(Path start, FileVisitOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<String> lines(Path path, Charset cs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<String> lines(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String readString(Path path) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String readString(Path path, Charset cs) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path writeString(Path path, CharSequence csq, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long mismatch(Path path, Path path2) throws IOException {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
