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

@SJDBInternal("GetInvoker")
public final class GetInvoker {

    @SuppressWarnings("exports")
    public static final Class<SJDBInternal> SJDBInternal_ANNOTATION_CLASS = SJDBInternal.class;

    private GetInvoker() {
        Class<?> c = GetInvoker.internalMethod0003a();
        if (!c.equals(GetInvoker.class)) {
            throw new SecurityException(
                    "Access denied to Class \'" + c.getName() + "\' to the \'GetInvoker\' Constructor");
        }
    }

    /**
     * This method gets the call stack using
     * {@link java.base/java.lang.Thread#getStackTrace() Thread.getStackTrace()} on
     * the current thread.
     * <p>
     * With the call stack it calls {@link #stackTraceToClass(StackTraceElement)}
     * 
     * @return The invoker of the class that invoked this method
     * 
     * @apiNote This method doesnot execute a lookup for the true caller, potentialy
     *          returning a class of a internal class of JDK
     *          <p>
     *          If you need to know what class invoked the method using reflection
     *          use
     *          {@link io.github.demnetwork.sjdb.internal.GetInvoker#lookupCallerClass()
     *          GetInvoker.lookupCallerClass()}
     */
    public static Class<?> getInvoker() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return stackTraceToClass(ste[Math.min((ste.length - 1), 3)]);
    }

    public static boolean invokedWithReflection() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String s = ste[Math.min((ste.length - 1), 3)].getClassName();
        if (s.contains("java.lang.reflect") || s.contains("jdk.internal.reflect")) {
            return true;
        }
        return false;
    }

    private static Class<?> stackTraceToClass(StackTraceElement element) {
        if (invokedWithReflection()) {
            throw new SecurityException("This method should not be invoked with Reflection.");
        }
        try {
            Class<?> c;
            c = Class.forName(element.getClassName());
            if (!c.isAnnotationPresent(SJDBInternal_ANNOTATION_CLASS)) {
                return c;
            }
            throw new SecurityException("The class is meant for internal usage of SJDB");
        } catch (ClassNotFoundException cnfe) {
            throw new Error("The class that invoked does not exist", cnfe);
        }
    }

    public static Class<?> lookupCallerClass() {
        if (GetInvoker.internalMethod0001a()) {
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            for (int i = 3; i < ste.length; i++) {
                String s = ste[i].getClassName();
                if (!(s.contains("java.lang.reflect") || s.contains("jdk.internal.reflect"))) {
                    return stackTraceToClass(ste[i]);
                }
            }
            throw new Error("Unable to find a Class that is not apart of Java Reflection.");
        } else {
            return GetInvoker.internalMethod0002a();
        }
    }

    private static boolean internalMethod0001a() {
        if (invokedWithReflection()) {
            throw new SecurityException("This method should not be invoked with Reflection.");
        }
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String s = ste[Math.min((ste.length - 1), 4)].getClassName();
        return (s.contains("java.lang.reflect") || s.contains("jdk.internal.reflect"));
    }

    static Class<?> internalMethod0002a() {
        if (invokedWithReflection()) {
            throw new SecurityException("This method should not be invoked with Reflection.");
        }
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return stackTraceToClass(ste[Math.min((ste.length - 1), 4)]);
    }

    static Class<?> internalMethod0003a() {
        if (invokedWithReflection()) {
            throw new SecurityException("This method should not be invoked with Reflection.");
        }
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        if (GetInvoker.internalMethod0001a()) {
            for (int i = 3; i < ste.length; i++) {
                String s = ste[i].getClassName();
                if (!(s.contains("java.lang.reflect") || s.contains("jdk.internal.reflect"))) {
                    return internalMethod0004a(ste[i]);
                }
            }
            throw new Error("Unable to find a Class that is not apart of Java Reflection.");
        } else {
            return GetInvoker.internalMethod0004a(ste[Math.min(ste.length - 1, 3)]);
        }
    }

    static Class<?> internalMethod0004a(StackTraceElement element) {
        if (invokedWithReflection()) {
            throw new SecurityException("This method should not be invoked with Reflection.");
        }
        try {
            Class<?> c;
            c = Class.forName(element.getClassName());
            return c;
        } catch (ClassNotFoundException cnfe) {
            throw new Error("The class that invoked does not exist", cnfe);
        }
    }
}
