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

package io.github.demnetwork.sjdb.dbelements.arrays;

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.github.demnetwork.sjdb.dbelements.DBElement;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;
import io.github.demnetwork.sjdb.dbelements.property.*;

public abstract class DBArray<T extends DBElement<?>> extends DBElement<T[]>
        implements Iterable<T>, MaxElementCountProperty, NameProperty {
    protected DBArray() {
    }

    public abstract void set(T[] data);

    public abstract void set(T data, int index);

    public abstract T[] get();

    public abstract T get(int index);

    public abstract String toString();

    public static <T extends DBElement<?>> DBArray<T> createArray(T[] cdata) {
        return new DBArray<T>() {
            private T[] data = cdata;
            private String name;

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int p = 0;

                    @Override
                    public boolean hasNext() {
                        return (p < data.length);
                    }

                    @Override
                    public T next() {
                        if (hasNext()) {
                            return data[p++];
                        }
                        throw new NoSuchElementException("The element at index " + p + "does not exist");
                    }

                };
            }

            @Override
            public int getMaxElementCount() {
                return this.data.length;
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public void setName(String name) {
                this.name = NameProperty.validateName(name);
            }

            @Override
            public void set(T[] data) {
                this.data = data;
            }

            @Override
            public void set(T data, int index) {
                this.data[index] = data;
            }

            @Override
            public T[] get() {
                return this.data;
            }

            @Override
            public T get(int index) {
                return this.data[index];
            }

            @Override
            public String toString() {
                DBRootElement dbre = new DBRootElement();
                dbre.set(data);
                return "[ARR][PROPERTIES] name=\'" + name + "\'; maxelementcount=" + this.data.length
                        + "; [/PROPERTIES][DATA]" + dbre.toString() + "[/DATA][/ARR]";
            }

        };
    }
}
