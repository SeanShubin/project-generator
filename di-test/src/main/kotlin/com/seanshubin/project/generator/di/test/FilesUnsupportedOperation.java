package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.FilesContract;

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

public class FilesUnsupportedOperation implements FilesContract {
    @Override
    public InputStream newInputStream(Path path, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path createLink(Path link, Path existing) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void delete(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean deleteIfExists(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path readSymbolicLink(Path link) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public FileStore getFileStore(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isSameFile(Path path, Path path2) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isHidden(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String probeContentType(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) {
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
    public FileTime getLastModifiedTime(Path path, LinkOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long size(Path path) {
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
    public Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path walkFileTree(Path start, FileVisitor<? super Path> visitor) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedReader newBufferedReader(Path path, Charset cs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedReader newBufferedReader(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long copy(InputStream in, Path target, CopyOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long copy(Path source, OutputStream out) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public byte[] readAllBytes(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> readAllLines(Path path, Charset cs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public List<String> readAllLines(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> list(Path dir) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> walk(Path start, FileVisitOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<String> lines(Path path, Charset cs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Stream<String> lines(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String readString(Path path) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String readString(Path path, Charset cs) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path writeString(Path path, CharSequence csq, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public long mismatch(Path path, Path path2) {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
