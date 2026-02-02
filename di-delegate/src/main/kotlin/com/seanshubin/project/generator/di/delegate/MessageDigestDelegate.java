package com.seanshubin.project.generator.di.delegate;

import com.seanshubin.project.generator.di.contract.MessageDigestContract;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.Provider;

//
// This file was imported from: ../kotlin-reusable
// Module: di-delegate
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class MessageDigestDelegate implements MessageDigestContract {
    private final MessageDigest delegate;

    public MessageDigestDelegate(MessageDigest delegate) {
        this.delegate = delegate;
    }

    @Override
    public Provider getProvider() {
        return delegate.getProvider();
    }

    @Override
    public void update(byte input) {
        delegate.update(input);
    }

    @Override
    public void update(byte[] input, int offset, int len) {
        delegate.update(input, offset, len);
    }

    @Override
    public void update(byte[] input) {
        delegate.update(input);
    }

    @Override
    public void update(ByteBuffer input) {
        delegate.update(input);
    }

    @Override
    public byte[] digest() {
        return delegate.digest();
    }

    @Override
    public int digest(byte[] buf, int offset, int len) throws DigestException {
        return delegate.digest(buf, offset, len);
    }

    @Override
    public byte[] digest(byte[] input) {
        return delegate.digest(input);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean isEqual(byte[] digesta, byte[] digestb) {
        return delegate.isEqual(digesta, digestb);
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public String getAlgorithm() {
        return delegate.getAlgorithm();
    }

    @Override
    public int getDigestLength() {
        return delegate.getDigestLength();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return delegate.clone();
    }
}
