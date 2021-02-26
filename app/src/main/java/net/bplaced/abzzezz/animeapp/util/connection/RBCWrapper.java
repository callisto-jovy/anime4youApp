/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.02.21, 13:28
 */

package net.bplaced.abzzezz.animeapp.util.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

/**
 * https://stackoverflow.com/questions/2263062/how-to-monitor-progress-jprogressbar-with-filechannels-transferfrom-method
 */
public class RBCWrapper implements ReadableByteChannel {

    private final Consumer<Integer> progressConsumer;
    private final int expectedSize;
    private final ReadableByteChannel rbc;
    private long readSoFar;

    public RBCWrapper(ReadableByteChannel rbc, int expectedSize, Consumer<Integer> progressConsumer) {
        this.progressConsumer = progressConsumer;
        this.expectedSize = expectedSize;
        this.rbc = rbc;
    }

    public void close() throws IOException {
        rbc.close();
    }

    public long getReadSoFar() {
        return readSoFar;
    }

    public boolean isOpen() {
        return rbc.isOpen();
    }

    public int read(ByteBuffer bb) throws IOException {
        int n;
        double progress;

        if ((n = rbc.read(bb)) > 0) {
            readSoFar += n;
            progress = expectedSize > 0 ? (double) readSoFar / (double) expectedSize * 100.0 : -1.0;
            progressConsumer.accept((int) progress);
        }
        return n;
    }
}