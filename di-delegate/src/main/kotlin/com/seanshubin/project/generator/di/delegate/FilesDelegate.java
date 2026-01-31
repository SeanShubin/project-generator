package com.seanshubin.project.generator.di.delegate;

import com.seanshubin.project.generator.di.contract.FilesContract;
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

public class FilesDelegate implements FilesContract {
    private final FilesContractChecked delegate;

    public FilesDelegate(FilesContractChecked delegate) {
        this.delegate = delegate;
    }

    private static FilesContract INSTANCE;

    public static FilesContract defaultInstance() {
        if (INSTANCE == null) {
            FilesContractChecked delegate = FilesDelegateChecked.defaultInstance();
            INSTANCE = new FilesDelegate(delegate);
        }
        return INSTANCE;
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options) {
        try {
            return delegate.newInputStream(path, options);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) {
        try {
            return delegate.newOutputStream(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) {
        try {
            return delegate.newByteChannel(path, options, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, OpenOption... options) {
        try {
            return delegate.newByteChannel(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir) {
        try {
            return delegate.newDirectoryStream(dir);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) {
        try {
            return delegate.newDirectoryStream(dir, glob);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) {
        try {
            return delegate.newDirectoryStream(dir, filter);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) {
        try {
            return delegate.createFile(path, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) {
        try {
            return delegate.createDirectory(dir, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) {
        try {
            return delegate.createDirectories(dir, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) {
        try {
            return delegate.createTempFile(dir, prefix, suffix, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) {
        try {
            return delegate.createTempFile(prefix, suffix, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) {
        try {
            return delegate.createTempDirectory(dir, prefix, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) {
        try {
            return delegate.createTempDirectory(prefix, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) {
        try {
            return delegate.createSymbolicLink(link, target, attrs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path createLink(Path link, Path existing) {
        try {
            return delegate.createLink(link, existing);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(Path path) {
        try {
            delegate.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteIfExists(Path path) {
        try {
            return delegate.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) {
        try {
            return delegate.copy(source, target, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) {
        try {
            return delegate.move(source, target, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path readSymbolicLink(Path link) {
        try {
            return delegate.readSymbolicLink(link);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public FileStore getFileStore(Path path) {
        try {
            return delegate.getFileStore(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isSameFile(Path path, Path path2) {
        try {
            return delegate.isSameFile(path, path2);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isHidden(Path path) {
        try {
            return delegate.isHidden(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public String probeContentType(Path path) {
        try {
            return delegate.probeContentType(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return delegate.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) {
        try {
            return delegate.readAttributes(path, type, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) {
        try {
            return delegate.setAttribute(path, attribute, value, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) {
        try {
            return delegate.getAttribute(path, attribute, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) {
        try {
            return delegate.readAttributes(path, attributes, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) {
        try {
            return delegate.getPosixFilePermissions(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms) {
        try {
            return delegate.setPosixFilePermissions(path, perms);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) {
        try {
            return delegate.getOwner(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) {
        try {
            return delegate.setOwner(path, owner);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isSymbolicLink(Path path) {
        return delegate.isSymbolicLink(path);
    }

    @Override
    public boolean isDirectory(Path path, LinkOption... options) {
        return delegate.isDirectory(path, options);
    }

    @Override
    public boolean isRegularFile(Path path, LinkOption... options) {
        return delegate.isRegularFile(path, options);
    }

    @Override
    public FileTime getLastModifiedTime(Path path, LinkOption... options) {
        try {
            return delegate.getLastModifiedTime(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) {
        try {
            return delegate.setLastModifiedTime(path, time);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public long size(Path path) {
        try {
            return delegate.size(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean exists(Path path, LinkOption... options) {
        return delegate.exists(path, options);
    }

    @Override
    public boolean notExists(Path path, LinkOption... options) {
        return delegate.notExists(path, options);
    }

    @Override
    public boolean isReadable(Path path) {
        return delegate.isReadable(path);
    }

    @Override
    public boolean isWritable(Path path) {
        return delegate.isWritable(path);
    }

    @Override
    public boolean isExecutable(Path path) {
        return delegate.isExecutable(path);
    }

    @Override
    public Path walkFileTree(Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor) {
        try {
            return delegate.walkFileTree(start, options, maxDepth, visitor);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path walkFileTree(Path start, FileVisitor<? super Path> visitor) {
        try {
            return delegate.walkFileTree(start, visitor);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public BufferedReader newBufferedReader(Path path, Charset cs) {
        try {
            return delegate.newBufferedReader(path, cs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public BufferedReader newBufferedReader(Path path) {
        try {
            return delegate.newBufferedReader(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options) {
        try {
            return delegate.newBufferedWriter(path, cs, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, OpenOption... options) {
        try {
            return delegate.newBufferedWriter(path, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public long copy(InputStream in, Path target, CopyOption... options) {
        try {
            return delegate.copy(in, target, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public long copy(Path source, OutputStream out) {
        try {
            return delegate.copy(source, out);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] readAllBytes(Path path) {
        try {
            return delegate.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<String> readAllLines(Path path, Charset cs) {
        try {
            return delegate.readAllLines(path, cs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<String> readAllLines(Path path) {
        try {
            return delegate.readAllLines(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... options) {
        try {
            return delegate.write(path, bytes, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) {
        try {
            return delegate.write(path, lines, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) {
        try {
            return delegate.write(path, lines, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Path> list(Path dir) {
        try {
            return delegate.list(dir);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) {
        try {
            return delegate.walk(start, maxDepth, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Path> walk(Path start, FileVisitOption... options) {
        try {
            return delegate.walk(start, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) {
        try {
            return delegate.find(start, maxDepth, matcher, options);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<String> lines(Path path, Charset cs) {
        try {
            return delegate.lines(path, cs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<String> lines(Path path) {
        try {
            return delegate.lines(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public String readString(Path path) {
        try {
            return delegate.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String readString(Path path, Charset cs) {
        try {
            return delegate.readString(path, cs);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Path writeString(Path path, CharSequence csq, OpenOption... options) {
        try {
            return delegate.writeString(path, csq, options);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) {
        try {
            return delegate.writeString(path, csq, cs, options);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public long mismatch(Path path, Path path2) {
        try {
            return delegate.mismatch(path, path2);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
