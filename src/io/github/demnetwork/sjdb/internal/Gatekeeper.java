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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;
import io.github.demnetwork.sjdb.internal.Warning.WarningDescription;

@SuppressWarnings("rawtypes")
@SJDBInternal("Gatekeeper")
public final class Gatekeeper {
    public static final Gatekeeper PUBLIC = new Gatekeeper(Gatekeeper.class, true, new Class[0]);
    public static final Gatekeeper PRIVATE = new Gatekeeper(Gatekeeper.class, false, new Class[0]);
    private final boolean blacklist;
    private final Class<?> owner;
    private final ClassList list;

    @SafeVarargs
    public Gatekeeper(boolean blacklist, Class... list) {
        this(GetInvoker.lookupCallerClass(), blacklist, list);
    }

    Gatekeeper(Class<?> owner, boolean blacklist, Class[] list) {
        if (GetInvoker.invokedWithReflection()) {
            throw new SecurityException("Cannot invoked this constructor with reflection");
        }
        if (list == null) {
            throw new NullPointerException("The Gatekeeper list is null");
        }
        this.owner = owner;
        this.list = new ClassList(Arrays.copyOf(list, list.length));
        this.blacklist = blacklist;
    }

    public boolean isTrusted(Class<?> clazz) {
        this.checkOwner(GetInvoker.internalMethod0003a());
        if (clazz.equals(this.owner)) {
            return true;
        }
        for (int i = 0; i < this.list.length; i++) {
            if (list.get(i).equals(clazz)) {
                return !this.blacklist;
            }
        }
        return this.blacklist;
    }

    @SuppressWarnings("unchecked")
    public <ExceptionType extends Exception> void checkTrust(Class<?> clazz, ExceptionType e)
            throws ExceptionType {
        this.checkOwner(GetInvoker.internalMethod0003a());
        if (!isTrusted(clazz)) {
            String s = e.getLocalizedMessage();
            if (s.contains("{clazz.name}")) {
                s = s.replace("{clazz.name}", clazz.getName());
                Throwable t = e.getCause();
                Class<ExceptionType> c = (Class<ExceptionType>) e.getClass();
                Constructor<ExceptionType> cns;
                try {
                    cns = c.getConstructor(String.class);
                    ExceptionType ex = cns.newInstance(s);
                    throw (ExceptionType) ex.initCause(t);
                } catch (Exception e2) {
                    try {
                        ExceptionType e3 = (ExceptionType) e2;
                        StackTraceElement[] ste = e3.getStackTrace();
                        for (int i = 0; i < ste.length; i++) {
                            Class<?> c2 = GetInvoker.internalMethod0004a(ste[i]);
                            if (c2.equals(Gatekeeper.class)) {
                                StackTraceElement[] ste2 = new StackTraceElement[ste.length - i];
                                System.arraycopy(ste, i, ste2, 0, ste2.length);
                                e3.setStackTrace(ste2);
                                throw e3;
                            }
                        }
                    } catch (ClassCastException e4) {
                    }
                    throw e;
                }
            }
            throw e;
        }
    }

    private void checkOwner(Class<?> c) {
        if (GetInvoker.invokedWithReflection()) {
            return;
        }
        if (!(c.equals(this.owner) || c.equals(Gatekeeper.class))) {
            throw new SecurityException("Only the owner can run this method");
        }
    }

    public boolean isOwner() {
        return this.isOwner(GetInvoker.lookupCallerClass());
    }

    private boolean isOwner(Class<?> clazz) {
        if (GetInvoker.invokedWithReflection()) {
            throw new SecurityException("You cannot invoked this method with reflection");
        }
        if (clazz == null) {
            throw new NullPointerException("Class is null");
        }
        return clazz.equals(owner);
    }

    public Gatekeeper setOwnership(Class<?> clazz) {
        if (this.isOwner(clazz)) {
            throw new IllegalStateException("Owner cannot set ownership to itself");
        }
        if (clazz == null) {
            throw new NullPointerException("Class is null");
        }
        return setOwnership(clazz, Nothing.class);
    }

    public Gatekeeper setOwnership(Class<?> clazz, Class... extraClassestoList) {
        if (clazz == null || extraClassestoList == null) {
            throw new NullPointerException("Atleast one of the arguments is(are) null");
        }
        if (clazz == this.owner && (extraClassestoList.length == 0)
                || (extraClassestoList.length == 1 && extraClassestoList[0] == Nothing.class)) {
            Warning w = new Warning(SJDBRuntime.getNextWarningID(), 3, "Heap pollution via creation of equal instances",
                    Warning.PerformanceCategory.CATEGORY);
            WarningDescription d1 = w
                    .createDescription("Using \'Gatekeeper.setOwnership\' by setting the owner to the current owner");
            WarningDescription d2 = w
                    .createDescription("and no changes to the list can introduce performance problems and may cause");
            WarningDescription d3 = w
                    .createDescription("heap pollution due unsed Objects in memory.");
            w.setDescription(new WarningDescription[] { d1, d2, d3 });
            SJDBRuntime.throwWarning(w);
        }
        if (extraClassestoList.length == 1 && extraClassestoList[0] == Nothing.class) {
            return new Gatekeeper(clazz, this.blacklist, this.list.toArray());
        }
        Class[] ec = Arrays.copyOf(extraClassestoList, extraClassestoList.length);
        final Class[] o = new Class[this.list.length + extraClassestoList.length];
        Class[] od = this.list.toArray();
        for (int i = 0; i < o.length; i++) {
            if (i < this.list.length) {
                o[i] = od[i];
            } else {
                o[i] = ec[i - this.list.length];
            }
        }
        return new Gatekeeper(clazz, this.blacklist, o);
    }

    public Gatekeeper setOwnership() {
        return this.setOwnership(GetInvoker.lookupCallerClass());
    }

    public Class<?> getOwner() {
        return owner;
    }

    public boolean isBlackList() {
        return blacklist;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject)
            return true;
        if (!(anObject instanceof Gatekeeper))
            return false;
        Gatekeeper obj = (Gatekeeper) anObject;
        return this.owner == obj.owner
                && (this.list == obj.list || Arrays.equals(this.list.toArray(), obj.list.toArray()));
    }

    @SuppressWarnings("unused")
    @SJDBInternal("Gatekeeper.ClassList")
    private static final class ClassList {
        private final Class slot1;
        private final Class slot2;
        private final Class slot3;
        private final Class slot4;
        private final Class slot5;
        private final Class slot6;
        private final Class slot7;
        private final Class slot8;
        private final ClassList nestedList;
        private final int length;

        private ClassList(final Class[] classes) {
            if (classes == null) {
                throw new NullPointerException("Cannot input null classes");
            }
            if (classes.length > 8) {
                this.slot1 = classes[0];
                this.slot2 = classes[1];
                this.slot3 = classes[2];
                this.slot4 = classes[3];
                this.slot5 = classes[4];
                this.slot6 = classes[5];
                this.slot7 = classes[6];
                this.slot8 = classes[7];
                final Class[] clazzes2 = new Class[classes.length - 8];
                System.arraycopy(classes, 7, clazzes2, 0, classes.length - 8);
                this.nestedList = new ClassList(clazzes2);
            } else if (classes.length == 8) {
                this.slot1 = classes[0];
                this.slot2 = classes[1];
                this.slot3 = classes[2];
                this.slot4 = classes[3];
                this.slot5 = classes[4];
                this.slot6 = classes[5];
                this.slot7 = classes[6];
                this.slot8 = classes[7];
                this.nestedList = null;
            } else if (classes.length < 8) {
                final Class[] clazzes2 = Arrays.copyOf(classes, 8);
                this.slot1 = clazzes2[0];
                this.slot2 = clazzes2[1];
                this.slot3 = clazzes2[2];
                this.slot4 = clazzes2[3];
                this.slot5 = clazzes2[4];
                this.slot6 = clazzes2[5];
                this.slot7 = clazzes2[6];
                this.slot8 = clazzes2[7];
                this.nestedList = null;
            } else {
                throw new AssertionError("Unable to check length");
            }
            this.length = classes.length;
        }

        private Class get(int index) {
            if (index >= length || index < 0)
                throw new ArrayIndexOutOfBoundsException("Index out of bounds for length " + this.length); // Avoid
                                                                                                           // recursive
                                                                                                           // operations
            if (index > 7) {
                if (this.nestedList != null) {
                    return nestedList.get(index - 8);
                } else {
                    throw new ArrayIndexOutOfBoundsException("Index out of bounds for length " + this.length);
                }
            } else {
                switch (index) {
                    case 0:
                        return this.slot1;
                    case 1:
                        return this.slot2;
                    case 2:
                        return this.slot3;
                    case 3:
                        return this.slot4;
                    case 4:
                        return this.slot5;
                    case 5:
                        return this.slot6;
                    case 6:
                        return this.slot7;
                    case 7:
                        return this.slot8;
                    default:
                        throw new IllegalArgumentException("Illegal index: " + index);
                }
            }
        }

        private int getLength() {
            return this.length;
        }

        private Class[] toArray() {
            Class[] array = new Class[this.length];
            for (int i = 0; i < this.length; i++) {
                array[i] = this.get(i);
            }
            return array;
        }
    }

    @SJDBInternal("Gatekeeper.GatekeeperLazyInit")
    public static final class GatekeeperLazyInit extends LazyInit<Gatekeeper> {
        private final Class<?> owner;

        public GatekeeperLazyInit(Object[] args) {
            super(args);
            if (args.length == 2) {
                LazyInit.checkType(args[0], Boolean.class, false);
                LazyInit.checkType(args[1], Class[].class, false);
            } else {
                throw new IllegalArrayLength(2, args.length);
            }
            this.owner = GetInvoker.internalMethod0003a();
        }

        public GatekeeperLazyInit(boolean blacklist, Class... list) {
            super(new Object[] { Boolean.valueOf(blacklist), list });
            this.owner = GetInvoker.internalMethod0003a();
        }

        @Override
        public Gatekeeper newInstance() {
            if (!super.initialized) {
                synchronized (super.lock) {
                    if (instance == null) {
                        super.instance = new Gatekeeper(owner,
                                ((Boolean) super.initArgs[0]).booleanValue(),
                                (Class[]) super.initArgs[1]);
                        super.initialized = true;
                    }
                    return super.instance;
                }
            } else if (super.instance != null) {
                return super.instance;
            } else {
                throw new GatekeeperLazyInitializationError("The Gatekeeper was initialized but its value is null");
            }
        }

        public static final class GatekeeperLazyInitializationError extends Error {
            private GatekeeperLazyInitializationError(String msg) {
                super(msg);
            }
        }
    }
}
