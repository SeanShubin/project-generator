package com.seanshubin.project.generator.di.contract;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.Provider;

public interface MessageDigestContract {
    Provider getProvider();

    void update(byte input);

    void update(byte[] input, int offset, int len);

    void update(byte[] input);

    void update(ByteBuffer input);

    byte[] digest();

    int digest(byte[] buf, int offset, int len) throws DigestException;

    byte[] digest(byte[] input);

    String toString();

    boolean isEqual(byte[] digesta, byte[] digestb);

    void reset();

    String getAlgorithm();

    int getDigestLength();

    Object clone() throws CloneNotSupportedException;
}

