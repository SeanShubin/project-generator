package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.MessageDigestContract;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.Provider;

//
// This file was imported from: ../kotlin-reusable
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class MessageDigestUnsupportedOperation implements MessageDigestContract {
    @Override
    public Provider getProvider() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void update(byte input) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void update(byte[] input, int offset, int len) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void update(byte[] input) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void update(ByteBuffer input) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public byte[] digest() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int digest(byte[] buf, int offset, int len) throws DigestException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public byte[] digest(byte[] input) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isEqual(byte[] digesta, byte[] digestb) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public String getAlgorithm() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public int getDigestLength() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
