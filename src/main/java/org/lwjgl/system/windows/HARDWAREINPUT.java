/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.windows;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Contains information about a simulated message generated by an input device other than a keyboard or mouse.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct HARDWAREINPUT {
 *     DWORD {@link #uMsg};
 *     WORD {@link #wParamL};
 *     WORD {@link #wParamH};
 * }</code></pre>
 */
public class HARDWAREINPUT extends Struct implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        UMSG,
        WPARAML,
        WPARAMH;

    static {
        Layout layout = __struct(
            __member(4),
            __member(2),
            __member(2)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        UMSG = layout.offsetof(0);
        WPARAML = layout.offsetof(1);
        WPARAMH = layout.offsetof(2);
    }

    /**
     * Creates a {@code HARDWAREINPUT} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public HARDWAREINPUT(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** the message generated by the input hardware */
    @NativeType("DWORD")
    public int uMsg() { return nuMsg(address()); }
    /** the low-order word of the {@code lParam} parameter for {@code uMsg} */
    @NativeType("WORD")
    public short wParamL() { return nwParamL(address()); }
    /** the high-order word of the {@code lParam} parameter for {@code uMsg} */
    @NativeType("WORD")
    public short wParamH() { return nwParamH(address()); }

    /** Sets the specified value to the {@link #uMsg} field. */
    public HARDWAREINPUT uMsg(@NativeType("DWORD") int value) { nuMsg(address(), value); return this; }
    /** Sets the specified value to the {@link #wParamL} field. */
    public HARDWAREINPUT wParamL(@NativeType("WORD") short value) { nwParamL(address(), value); return this; }
    /** Sets the specified value to the {@link #wParamH} field. */
    public HARDWAREINPUT wParamH(@NativeType("WORD") short value) { nwParamH(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public HARDWAREINPUT set(
        int uMsg,
        short wParamL,
        short wParamH
    ) {
        uMsg(uMsg);
        wParamL(wParamL);
        wParamH(wParamH);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public HARDWAREINPUT set(HARDWAREINPUT src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code HARDWAREINPUT} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static HARDWAREINPUT malloc() {
        return wrap(HARDWAREINPUT.class, nmemAllocChecked(SIZEOF));
    }

    /** Returns a new {@code HARDWAREINPUT} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static HARDWAREINPUT calloc() {
        return wrap(HARDWAREINPUT.class, nmemCallocChecked(1, SIZEOF));
    }

    /** Returns a new {@code HARDWAREINPUT} instance allocated with {@link BufferUtils}. */
    public static HARDWAREINPUT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return wrap(HARDWAREINPUT.class, memAddress(container), container);
    }

    /** Returns a new {@code HARDWAREINPUT} instance for the specified memory address. */
    public static HARDWAREINPUT create(long address) {
        return wrap(HARDWAREINPUT.class, address);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static HARDWAREINPUT createSafe(long address) {
        return address == NULL ? null : wrap(HARDWAREINPUT.class, address);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity) {
        return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity) {
        return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return wrap(Buffer.class, memAddress(container), capacity, container);
    }

    /**
     * Create a {@link Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static Buffer create(long address, int capacity) {
        return wrap(Buffer.class, address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : wrap(Buffer.class, address, capacity);
    }

    // -----------------------------------

    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static HARDWAREINPUT mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static HARDWAREINPUT callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static HARDWAREINPUT mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static HARDWAREINPUT callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code HARDWAREINPUT} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static HARDWAREINPUT malloc(MemoryStack stack) {
        return wrap(HARDWAREINPUT.class, stack.nmalloc(ALIGNOF, SIZEOF));
    }

    /**
     * Returns a new {@code HARDWAREINPUT} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static HARDWAREINPUT calloc(MemoryStack stack) {
        return wrap(HARDWAREINPUT.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #uMsg}. */
    public static int nuMsg(long struct) { return UNSAFE.getInt(null, struct + HARDWAREINPUT.UMSG); }
    /** Unsafe version of {@link #wParamL}. */
    public static short nwParamL(long struct) { return UNSAFE.getShort(null, struct + HARDWAREINPUT.WPARAML); }
    /** Unsafe version of {@link #wParamH}. */
    public static short nwParamH(long struct) { return UNSAFE.getShort(null, struct + HARDWAREINPUT.WPARAMH); }

    /** Unsafe version of {@link #uMsg(int) uMsg}. */
    public static void nuMsg(long struct, int value) { UNSAFE.putInt(null, struct + HARDWAREINPUT.UMSG, value); }
    /** Unsafe version of {@link #wParamL(short) wParamL}. */
    public static void nwParamL(long struct, short value) { UNSAFE.putShort(null, struct + HARDWAREINPUT.WPARAML, value); }
    /** Unsafe version of {@link #wParamH(short) wParamH}. */
    public static void nwParamH(long struct, short value) { UNSAFE.putShort(null, struct + HARDWAREINPUT.WPARAMH, value); }

    // -----------------------------------

    /** An array of {@link HARDWAREINPUT} structs. */
    public static class Buffer extends StructBuffer<HARDWAREINPUT, Buffer> implements NativeResource {

        private static final HARDWAREINPUT ELEMENT_FACTORY = HARDWAREINPUT.create(-1L);

        /**
         * Creates a new {@code HARDWAREINPUT.Buffer} instance backed by the specified container.
         *
         * Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link HARDWAREINPUT#SIZEOF}, and its mark will be undefined.
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected HARDWAREINPUT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@link HARDWAREINPUT#uMsg} field. */
        @NativeType("DWORD")
        public int uMsg() { return HARDWAREINPUT.nuMsg(address()); }
        /** @return the value of the {@link HARDWAREINPUT#wParamL} field. */
        @NativeType("WORD")
        public short wParamL() { return HARDWAREINPUT.nwParamL(address()); }
        /** @return the value of the {@link HARDWAREINPUT#wParamH} field. */
        @NativeType("WORD")
        public short wParamH() { return HARDWAREINPUT.nwParamH(address()); }

        /** Sets the specified value to the {@link HARDWAREINPUT#uMsg} field. */
        public Buffer uMsg(@NativeType("DWORD") int value) { HARDWAREINPUT.nuMsg(address(), value); return this; }
        /** Sets the specified value to the {@link HARDWAREINPUT#wParamL} field. */
        public Buffer wParamL(@NativeType("WORD") short value) { HARDWAREINPUT.nwParamL(address(), value); return this; }
        /** Sets the specified value to the {@link HARDWAREINPUT#wParamH} field. */
        public Buffer wParamH(@NativeType("WORD") short value) { HARDWAREINPUT.nwParamH(address(), value); return this; }

    }

}