/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile;

import com.ranttu.rapid.personalweb.core.wasm.exception.UnexpectedEOF;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmUnknownError;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * the source input stream
 *
 * @author rapid
 * @version $Id: WasmSourceStream.java, v 0.1 2018-12-08- 1:58 PM rapid Exp $
 */
@RequiredArgsConstructor(staticName = "of")
class WasmSourceStream {
    /**
     * raw input stream
     */
    private final InputStream ins;

    /**
     * buffered bytes
     */
    private Queue<Byte> byteBuff = new LinkedList<>();

    /**
     * pick a unsigned integer, and move pointer to next byte
     * formatted in LEB128
     */
    public long nextUInt() {
        long result = 0;
        int shift = 0;
        byte b;

        do {
            ensureHas(1);
            b = byteBuff.poll();
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) == 0x80);

        return result;
    }

    /**
     * can be i32 or i64
     * TODO: length check
     */
    public long nextSNumber() {
        long result = 0;
        int shift = 0;
        byte b;

        do {
            ensureHas(1);
            b = byteBuff.poll();

            byte current = (byte) (b & 0x7F);
            if (current >= 0x40) {
                current -= 0x80;
            }

            result |= current << shift;
            shift += 7;
        } while ((b & 0x80) == 0x80);

        return result;
    }

    public float nextF32() {
        ensureHas(4);

        return Float.intBitsToFloat(byteBuff.poll()
            | (byteBuff.poll() << 8)
            | (byteBuff.poll() << 16)
            | (byteBuff.poll() << 24));
    }

    public double nextF64() {
        ensureHas(8);

        return Float.intBitsToFloat(byteBuff.poll()
            | (byteBuff.poll() << 8L)
            | (byteBuff.poll() << 16L)
            | (byteBuff.poll() << 24L)
            | (byteBuff.poll() << 32L)
            | (byteBuff.poll() << 40L)
            | (byteBuff.poll() << 48L)
            | (byteBuff.poll() << 56L));
    }

    public void readBytes(byte[] buff) {
        int read = 0;
        // TODO: support unsigned int length
        int length = buff.length;

        // flush buff
        while (byteBuff.size() > 0 && read < length) {
            buff[read] = byteBuff.poll();
            read++;
        }

        if (read < length) {
            try {
                read += ins.read(buff, read, length - read);
            } catch (IOException e) {
                throw new WasmUnknownError("when reading from source", e);
            }
        }

        if (read != length) {
            throw new UnexpectedEOF("section should has length of " + length);
        }
    }

    /**
     * read a sub stream
     */
    public WasmSourceStream asSubStream(long length) {
        // TODO: support unsigned int length
        byte[] buff = new byte[(int) length];
        readBytes(buff);

        // create sub stream
        return new WasmSourceStream(new ByteArrayInputStream(buff));
    }

    /**
     * peek a byte
     */
    public byte peek() {
        ensureHas(1);
        return byteBuff.peek();
    }

    /**
     * pick a byte, and move pointer to next
     */
    public byte next() {
        ensureHas(1);
        return byteBuff.poll();
    }

    /**
     * has next byte to read
     */
    public boolean hasNext() {
        if (byteBuff.size() > 0) {
            return true;
        }

        int res = readNext();
        if (res < 0) {
            return false;
        } else {
            byteBuff.add((byte) res);
            return true;
        }
    }


    private int readNext() {
        int res;
        try {
            res = ins.read();
        } catch (IOException e) {
            throw new WasmUnknownError("when reading from source", e);
        }

        return res;
    }

    /**
     * ensure there is one byte in buff
     */
    private void ensureHas(int cnt) {
        while (byteBuff.size() < cnt) {
            int res = readNext();
            if (res < 0) {
                throw new UnexpectedEOF("unexpected end of file when reading from source");
            }
            byteBuff.add((byte) res);
        }
    }
}