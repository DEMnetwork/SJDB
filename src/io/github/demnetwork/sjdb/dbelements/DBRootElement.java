/*
 *   Copyright (c) 2024 DEMnetwork
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

package io.github.demnetwork.sjdb.dbelements;

import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;
import io.github.demnetwork.sjdb.dbelements.support.SupportsChildren;

public final class DBRootElement extends DBElement<Object[]> implements SupportsChildren {
    private Object[] data;

    public DBRootElement() {
        this.data = new Object[16384];
    }

    public DBRootElement(int length) {
        this.data = new Object[length];
    }

    @Override
    public Object[] get() {
        return this.data;
    }

    @Override
    public void set(Object[] data) {
        this.data = data;
    }

    public void set(int index, Object data) {
        if (index >= this.data.length || index <= 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            this.data[index] = data;
        }
    }

    public Object get(int index) {
        if (index >= this.data.length || index <= 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            return this.data[index];
        }
    }

    /**
     * This method allows the user to get a
     * {@link io.github.demnetwork.sjdb.dbelements.DBElement DBElement} with a name.
     * <p>
     * This checks if the {@link java.lang.Object} is an instance of
     * {@link io.github.demnetwork.sjdb.dbelements.property.NameProperty
     * NameProperty} and {@link io.github.demnetwork.sjdb.dbelements.DBElement
     * DBElement}. if it is it checks if the name is equals the Name argument
     * 
     * @throws NullPointerException   If the Name is null.
     * @throws NoSuchElementException If the Object is not found
     * 
     * @since SJDB v1.0.0
     */
    @Override
    public DBElement<?> getByName(String Name) throws NullPointerException, NoSuchElementException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof NameProperty) {
                NameProperty np = (NameProperty) data[i];
                if (np.getName().equals(Name) && np instanceof DBElement<?>) {
                    return (DBElement<?>) data[i];
                }
            }
        }
        throw new NoSuchElementException("The element with name \'" + Name + "\' was not found");
    }

    @Override
    public boolean hasObjectWithName(String Name) throws NullPointerException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof NameProperty) {
                NameProperty np = (NameProperty) data[i];
                if (np.getName().equals(Name) && np instanceof DBElement<?>) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public DBElement<?> getByName(String Name, int depth)
            throws NullPointerException, NoSuchElementException, IllegalArgumentException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        int cDepth = depth;
        if (cDepth <= 0) {
            throw new IllegalArgumentException("Depth cannot be less than or equal 0");
        }
        for (int i = 0; (!(i >= this.data.length)); i++) {
            if (this.data[i] instanceof DBElement<?>) {
                if (data[i] instanceof NameProperty) {
                    NameProperty np = (NameProperty) data[i];
                    if (np.getName().equals(Name)) {
                        return (DBElement<?>) data[i];
                    }
                }
                if (data[i] instanceof SupportsChildren) {
                    if ((cDepth - 1) >= 0) {
                        cDepth = cDepth - 1;
                        SupportsChildren sc = (SupportsChildren) data[i];
                        try {
                            return sc.getByName(Name, cDepth);
                        } catch (NoSuchElementException err) {
                            cDepth = cDepth + 1;
                        }
                    }
                }
            }
        }
        throw new NoSuchElementException("The element with name \'" + Name + "\' was not found");
    }

    @Override
    public boolean hasObjectWithName(String Name, int depth) throws NullPointerException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        if (depth <= 0) {
            throw new IllegalArgumentException("Depth cannot be less than or equal 0");
        }
        int cDepth = depth;
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof DBElement<?>) {
                if (data[i] instanceof NameProperty) {
                    NameProperty np = (NameProperty) data[i];
                    if (np.getName().equals(Name)) {
                        return true;
                    }
                }
                if (data[i] instanceof SupportsChildren) {
                    if ((cDepth - 1) >= 1) {
                        SupportsChildren sc = (SupportsChildren) data[i];
                        if (sc.hasObjectWithName(Name, (cDepth - 1))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
