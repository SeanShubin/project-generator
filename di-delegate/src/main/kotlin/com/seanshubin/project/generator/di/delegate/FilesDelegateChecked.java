package com.seanshubin.project.generator.di.delegate;

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
// Module: di-delegate
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class FilesDelegateChecked implements FilesContractChecked {
    private static FilesContractChecked INSTANCE;

    public static FilesContractChecked defaultInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilesDelegateChecked();
        }
        return INSTANCE;
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        return Files.newInputStream(path, options);
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
        return Files.newOutputStream(path, options);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return Files.newByteChannel(path, options, attrs);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws IOException {
        return Files.newByteChannel(path, options);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir) throws IOException {
        return Files.newDirectoryStream(dir);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) throws IOException {
        return Files.newDirectoryStream(dir, glob);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return Files.newDirectoryStream(dir, filter);
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) throws IOException {
        return Files.createFile(path, attrs);
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        return Files.createDirectory(dir, attrs);
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) throws IOException {
        return Files.createDirectories(dir, attrs);
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
        return Files.createTempFile(dir, prefix, suffix, attrs);
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
        return Files.createTempFile(prefix, suffix, attrs);
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException {
        return Files.createTempDirectory(dir, prefix, attrs);
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IOException {
        return Files.createTempDirectory(prefix, attrs);
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        return Files.createSymbolicLink(link, target, attrs);
    }

    @Override
    public Path createLink(Path link, Path existing) throws IOException {
        return Files.createLink(link, existing);
    }

    @Override
    public void delete(Path path) throws IOException {
        Files.delete(path);
    }

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) throws IOException {
        return Files.copy(source, target, options);
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) throws IOException {
        return Files.move(source, target, options);
    }

    @Override
    public Path readSymbolicLink(Path link) throws IOException {
        return Files.readSymbolicLink(link);
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return Files.getFileStore(path);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return Files.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return Files.isHidden(path);
    }

    @Override
    public String probeContentType(Path path) throws IOException {
        return Files.probeContentType(path);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return Files.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return Files.readAttributes(path, type, options);
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        return Files.setAttribute(path, attribute, value, options);
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) throws IOException {
        return Files.getAttribute(path, attribute, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return Files.readAttributes(path, attributes, options);
    }

    @Override
    public Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) throws IOException {
        return Files.getPosixFilePermissions(path, options);
    }

    @Override
    public Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms) throws IOException {
        return Files.setPosixFilePermissions(path, perms);
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) throws IOException {
        return Files.getOwner(path, options);
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) throws IOException {
        return Files.setOwner(path, owner);
    }

    @Override
    public boolean isSymbolicLink(Path path) {
        return Files.isSymbolicLink(path);
    }

    @Override
    public boolean isDirectory(Path path, LinkOption... options) {
        return Files.isDirectory(path, options);
    }

    @Override
    public boolean isRegularFile(Path path, LinkOption... options) {
        return Files.isRegularFile(path, options);
    }

    @Override
    public FileTime getLastModifiedTime(Path path, LinkOption... options) throws IOException {
        return Files.getLastModifiedTime(path, options);
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) throws IOException {
        return Files.setLastModifiedTime(path, time);
    }

    @Override
    public long size(Path path) throws IOException {
        return Files.size(path);
    }

    @Override
    public boolean exists(Path path, LinkOption... options) {
        return Files.exists(path, options);
    }

    @Override
    public boolean notExists(Path path, LinkOption... options) {
        return Files.notExists(path, options);
    }

    @Override
    public boolean isReadable(Path path) {
        return Files.isReadable(path);
    }

    @Override
    public boolean isWritable(Path path) {
        return Files.isWritable(path);
    }

    @Override
    public boolean isExecutable(Path path) {
        return Files.isExecutable(path);
    }

    @Override
    public Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor) throws IOException {
        return Files.walkFileTree(start, options, maxDepth, visitor);
    }

    @Override
    public Path walkFileTree(Path start, FileVisitor<? super Path> visitor) throws IOException {
        return Files.walkFileTree(start, visitor);
    }

    @Override
    public BufferedReader newBufferedReader(Path path, Charset cs) throws IOException {
        return Files.newBufferedReader(path, cs);
    }

    @Override
    public BufferedReader newBufferedReader(Path path) throws IOException {
        return Files.newBufferedReader(path);
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options) throws IOException {
        return Files.newBufferedWriter(path, cs, options);
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, OpenOption... options) throws IOException {
        return Files.newBufferedWriter(path, options);
    }

    @Override
    public long copy(InputStream in, Path target, CopyOption... options) throws IOException {
        return Files.copy(in, target, options);
    }

    @Override
    public long copy(Path source, OutputStream out) throws IOException {
        return Files.copy(source, out);
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public List<String> readAllLines(Path path, Charset cs) throws IOException {
        return Files.readAllLines(path, cs);
    }

    @Override
    public List<String> readAllLines(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        return Files.write(path, bytes, options);
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) throws IOException {
        return Files.write(path, lines, options);
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IOException {
        return Files.write(path, lines, options);
    }

    @Override
    public Stream<Path> list(Path dir) throws IOException {
        return Files.list(dir);
    }

    @Override
    public Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) throws IOException {
        return Files.walk(start, maxDepth, options);
    }

    @Override
    public Stream<Path> walk(Path start, FileVisitOption... options) throws IOException {
        return Files.walk(start, options);
    }

    @Override
    public Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return Files.find(start, maxDepth, matcher, options);
    }

    @Override
    public Stream<String> lines(Path path, Charset cs) throws IOException {
        return Files.lines(path, cs);
    }

    @Override
    public Stream<String> lines(Path path) throws IOException {
        return Files.lines(path);
    }

    @Override
    public String readString(Path path) throws IOException {
        return Files.readString(path);
    }

    @Override
    public String readString(Path path, Charset cs) throws IOException {
        return Files.readString(path, cs);
    }

    @Override
    public Path writeString(Path path, CharSequence csq, OpenOption... options) throws IOException {
        return Files.writeString(path, csq, options);
    }

    @Override
    public Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) throws IOException {
        return Files.writeString(path, csq, cs, options);
    }

    @Override
    public long mismatch(Path path, Path path2) throws IOException {
        return Files.mismatch(path, path2);
    }
}
