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
import io.github.demnetwork.sjdb.internal.Gatekeeper.GatekeeperLazyInit;

@SJDBInternal("LazyInit")
public abstract class LazyInit<T> {
    /**
     * Static factory for LazyInit
     * 
     * @apiNote The type returned by this method will handle ambiguty by taking the
     *          first constructor that matches
     *
     * @param type Type of LazyInitalizer
     * @param args Arguments for initalization
     * @return a instance of
     *         {@link io.github.demnetwork.sjdb.internal.LazyInit.BasicLazyInit
     *         BasicLazyInit}
     */
    public static <T> LazyInit<T> getLazyInitializer(Class<T> type, Object[] args) {
        return getLazyInitializer(type, args, false);
    }

    /**
     * Static factory for LazyInit
     * 
     * @apiNote The type returned by this method will handle ambiguty by taking the
     *          first constructor that matches
     *
     * @param type               Type of to LazyInit
     * @param args               Arguments for initalization
     * @param lookupConstructors toggles the lookup of the constructors
     * @return a instance of
     *         {@link io.github.demnetwork.sjdb.internal.LazyInit.BasicLazyInit
     *         BasicLazyInit}
     */
    @SuppressWarnings("unchecked")
    public static <T> LazyInit<T> getLazyInitializer(Class<T> type, Object[] args, boolean lookupConstructors) {
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        if (args == null) {
            throw new NullPointerException("The argument array is assinged null");
        }
        if (type != Gatekeeper.class)
            return new BasicLazyInit<>(args, type, lookupConstructors);
        return (LazyInit<T>) new GatekeeperLazyInit(args);
    }

    protected boolean initialized;
    protected final Object[] initArgs;
    protected T instance;
    protected final Object lock;

    protected LazyInit(Object[] args) {
        this.lock = new Object();
        this.initialized = false;
        this.initArgs = args.clone();
        this.instance = null;
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T checkType(Object o, Class<T> type, boolean allowNull)
            throws IllegalArgumentException, NullPointerException {
        if (o == null && !allowNull) {
            throw new NullPointerException("Null Values are not allowed");
        } else if (o == null) {
            return null;
        }
        if (type.isAssignableFrom(o.getClass())) {
            return (T) o;
        }
        throw new IllegalArgumentException(
                "The Type " + o.getClass().getName() + " cannot cast to Type " + type.getName());
    }

    public final boolean isInitialized() {
        return initialized;
    }

    public T getInstance() {
        if (!this.initialized || this.instance == null)
            throw new IllegalStateException(
                    "The current instance was not instantiated and/or it was instantiated and its value it null");
        return this.instance;
    }

    public abstract T newInstance() throws Exception;

    private static final class BasicLazyInit<T> extends LazyInit<T> {
        private final Class<T> type;
        private final Constructor<T> c;

        @SuppressWarnings("unchecked")
        private BasicLazyInit(Object[] args, Class<T> type, boolean lookupConstructors) {
            super(args);
            Constructor<T> cns = null;
            if (lookupConstructors) {
                Constructor<T>[] cs = (Constructor<T>[]) type.getConstructors();
                Constructor<T> f = null;
                if (cs.length == 0) {
                    throw new IllegalArgumentException("The type has no public Constructors");
                }
                for (int i = 0; i < cs.length; i++) {
                    Constructor<T> c = cs[i];
                    Class<?>[] clazzes = c.getParameterTypes();
                    if (check(clazzes)) {
                        cns = c;
                        f = cns;
                        break;
                    }
                }
                if (f == null) {
                    throw new IllegalArgumentException("Invalid Construtor or invalid arguments");
                }
            }
            this.c = cns;
            this.type = type;
        }

        @Override
        public T newInstance() throws Exception {
            if (!super.initialized) {
                synchronized (super.lock) {
                    if (super.instance == null) {
                        if (c == null) {
                            Constructor<T> c = type.getConstructor(getTypes());
                            super.instance = c.newInstance(super.initArgs);
                            super.initialized = true;
                        } else {
                            super.instance = this.c.newInstance(super.initArgs);
                            super.initialized = true;
                        }
                    }
                    return super.instance;
                }
            } else if (super.instance != null) {
                return super.instance;
            } else {
                throw new Error("The Current LazyInit had already initalized one instance of Type \'"
                        + type.getName() + "\' but its value is null");
            }
        }

        private final Class<?>[] getTypes() {
            Class<?>[] clazzes = new Class[super.initArgs.length];
            for (int i = 0; i++ < super.initArgs.length;) {
                if (super.initArgs[i] != null) {
                    clazzes[i] = super.initArgs[i].getClass();
                } else {
                    clazzes[i] = Object.class;
                }

            }
            return clazzes;
        }

        private final Class<?>[] getTypes2() {
            Class<?>[] clazzes = new Class[super.initArgs.length];
            for (int i = 0; i < super.initArgs.length; i++) {
                if (super.initArgs[i] != null) {
                    clazzes[i] = super.initArgs[i].getClass();
                } else {
                    clazzes[i] = null;
                }

            }
            return clazzes;
        }

        private final boolean check(Class<?>[] types) {
            Class<?>[] thisTypes = getTypes2();
            if (thisTypes.length != types.length)
                return false;
            if (thisTypes == types)
                return true;
            for (int i = 0; i < types.length; i++) {
                /*
                 * System.out.println("thisTypes[" + i + "]: " + thisTypes[i].getName() + ";
                 * // types[" + i + "]: "
                 * + types[i].getName() + ";");
                 */
                if (thisTypes[i] == null) {
                    if (types[i].isPrimitive()) {
                        return false;
                    }
                } else if (!convertClass(types[i]).isAssignableFrom(thisTypes[i])) {
                    return false;
                }
            }
            return true;
        }

        private static Class<?> convertClass(Class<?> clazz) {
            if (clazz == int.class) {
                return Integer.class;
            } else if (clazz == long.class) {
                return Long.class;
            } else if (clazz == double.class) {
                return Double.class;
            } else if (clazz == float.class) {
                return Float.class;
            } else if (clazz == boolean.class) {
                return Boolean.class;
            } else if (clazz == char.class) {
                return Character.class;
            } else if (clazz == byte.class) {
                return Byte.class;
            } else if (clazz == short.class) {
                return Short.class;
            } else {
                return clazz;
            }
        }
    }
}
