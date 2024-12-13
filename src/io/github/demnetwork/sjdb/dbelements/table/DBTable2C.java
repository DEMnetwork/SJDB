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

package io.github.demnetwork.sjdb.dbelements.table;

import io.github.demnetwork.sjdb.dbelements.DBElement;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;

/**
 * Used to make a new Table with 2 Columns
 */
public class DBTable2C<Col1, Col2> extends DBElement<Object> implements NameProperty {
    private String name;
    private Col1[] Col1;
    private Col2[] Col2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set(Col1 Column1, Col2 Column2, int index) {
        Col1[index] = Column1;
        Col2[index] = Column2;
    }

    @Override
    public Object get() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method \'get()\' is not supported here");
    }

    @Override
    public void set(Object data) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method \'set(Object)\' is not supported here");
    }
}
