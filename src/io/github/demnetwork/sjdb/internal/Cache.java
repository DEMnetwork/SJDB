/*
 *   Copyright (c) 2025 DEMnetwork
 *   All rights reserved.

 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package io.github.demnetwork.sjdb.internal;

import java.lang.reflect.*;

/**
 * WIP class for caching
 */
@SuppressWarnings("unused")
@SJDBInternal("Cache")
public final class Cache<T> {
    private final T[] data;
    private final Gatekeeper gk;

    public Cache(T[] data) {
        this(data, Nothing.class);
    }

    @SuppressWarnings({ "rawtypes" })
    public Cache(T[] data, Class... cacheAccess) {
        if (cacheAccess == null || data == null) {
            throw new NullPointerException("Null value(s) found!");
        }
        this.data = java.util.Arrays.copyOf(data, data.length);
        final Class[] classes = new Class[cacheAccess.length + 1];
        System.arraycopy(cacheAccess, 0, classes, 1, cacheAccess.length);
        classes[0] = GetInvoker.internalMethod0002a();
        this.gk = new Gatekeeper(Cache.class, false, classes);
    }

    public T getData(int index) {
        if (data == null) {
            throw new IllegalStateException("Disposed Cache");
        }
        gk.checkTrust(GetInvoker.lookupCallerClass(),
                new SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache"));
        return data[index];
    }

    public void set(T data, int index) {
        if (data == null) {
            throw new IllegalStateException("Disposed Cache");
        }
        gk.checkTrust(GetInvoker.lookupCallerClass(),
                new SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache"));
        if (data != null) {
            this.data[index] = data;
        } else
            throw new NullPointerException("Null data");
    }

    public void reset(int index) {
        if (data == null) {
            throw new IllegalStateException("Disposed Cache");
        }
        gk.checkTrust(GetInvoker.lookupCallerClass(),
                new SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache"));
        this.data[index] = null;
    }

    /*
     * public void dispose() {
     * gk.checkTrust(GetInvoker.lookupCallerClass(),
     * new
     * SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache")
     * );
     * dispose0();
     * }
     * 
     * private void dispose0() {
     * try {
     * gk.checkTrust(GetInvoker.internalMethod0003a(),
     * new
     * SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache")
     * );
     * Field f = this.getClass().getDeclaredField("data");
     * f.setAccessible(true);
     * Constructor<Field> c = (Constructor<Field>)
     * Field.class.getDeclaredConstructor(Class.class, String.class,
     * Class.class, int.class, boolean.class, int.class, String.class,
     * byte[].class);
     * c.setAccessible(true);
     * Field f2 = c.newInstance(f.getDeclaringClass(), f.getName(), f.getType(),
     * f.getModifiers() & ~Modifier.FINAL,
     * false, 0, "", new byte[0]);
     * f2.setAccessible(true);
     * f2.set(this, null);
     * } catch (Exception e) {
     * throw new RuntimeException("Something went wrong while disposing cache", e);
     * }
     * }
     * 
     * public boolean disposed() {
     * gk.checkTrust(GetInvoker.lookupCallerClass(),
     * new
     * SecurityException("Access of Class \'{clazz.name}\' is not allowed to cache")
     * );
     * return data == null;
     * }
     */
}
