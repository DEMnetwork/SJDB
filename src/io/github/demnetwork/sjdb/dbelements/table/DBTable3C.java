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

package io.github.demnetwork.sjdb.dbelements.table;

import io.github.demnetwork.sjdb.dbelements.*;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;

@SuppressWarnings("unused")
public class DBTable3C<Col_1 extends DBElement<?>, Col_2 extends DBElement<?>, Col_3 extends DBElement<?>>
        extends DBTable2C<Col_1, DBTable2C<Col_2, Col_3>> {

    @SuppressWarnings(value = { "unchecked" })
    public DBTable3C(Col_1[] Column1, Col_2[] Column2, Col_3[] Column3, String name) {
        super(name, Column1, new DBTable2C[1]);
        super.setCol2(new DBTable2C<>(Column2, Column3), 0);
        if (Column1.length != Column2.length || Column1.length != Column3.length || Column2.length != Column3.length) {
            throw new IllegalArgumentException("The lengths are not equal!");
        }
    }

    @SuppressWarnings(value = { "unchecked" })
    protected DBTable3C(String name, Col_1[] Column1, Col_2[] Column2, Col_3[] Column3) {
        super(name, Column1, new DBTable2C[1]);
        super.setCol2(new DBTable2C<>(Column2, Column3), 0);
    }

    private DBTable3C(Col_1[] Column1, Col_2[] Column2, String Name) {
    }

    private DBTable3C(Col_1[] Column1, Col_2[] Column2) {
    }

    private DBTable3C() {
    }

    private DBTable3C(int length) {
    }

    @Override
    public DBElement<?>[] get(int index) {
        return new DBElement<?>[] { super.getCol1(index), super.getCol2(0).getCol1(index),
                super.getCol2(0).getCol2(index) };
    }

    @Override
    public DBTable2C<Col_2, Col_3> getCol2(int index) {
        throw new UnsupportedOperationException("This method is not supported use \'getColumn2(int)\' instead");
    }

    public Col_1 getColumn1(int index) {
        return super.getCol1(index);
    }

    public Col_2 getColumn2(int index) {
        return super.getCol2(0).getCol1(index);
    }

    public Col_3 getColumn3(int index) {
        return super.getCol2(0).getCol2(index);
    }

    public void setColumn1(Col_1 data, int index) {
        super.setCol1(data, index);
    }

    public void setColumn2(Col_2 data, int index) {
        super.getCol2(0).setCol1(data, index);
    }

    public void setColumn3(Col_3 data, int index) {
        super.getCol2(0).setCol2(data, index);
    }

    @Override
    public DBElement<?> get(int index, int col) {
        if (col == 1) {
            return super.get(index, col);
        } else if (col == 2 || col == 3) {
            return super.getCol2(0).get(index, col - 1);
        } else {
            throw new IllegalArgumentException("Invalid Column");
        }
    }

    @Override
    public String toString() {
        String name = super.getName();
        Col_2[] col2 = super.getCol2(0).Col1;
        Col_3[] col3 = super.getCol2(0).Col2;
        String s = "[TABLE3C][PROPERTIES] name=" + name + "; maxelementcount=" + super.length + "; [/PROPERTIES][COL1]";
        DBRootElement dbre = new DBRootElement(this.Col1.length);
        dbre.set(Col1);
        s = s + dbre.toString();
        s = s + "[/COL1][COL2]";
        dbre.set(col2);
        s = s + dbre.toString();
        s = s + "[/COL2][COL3]";
        dbre.set(col3);
        s = s + dbre.toString();
        s = s + "[/COL3][/TABLE3C]";
        return s;
    }

    public void set(Col_1 Col1, Col_2 Col2, Col_3 Col3, int index) {
        Table.checkNull(Col1, Col2, Col3);
        this.setColumn1(Col1, index);
        this.setColumn2(Col2, index);
        this.setColumn3(Col3, index);
        return;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(DBElement<?>[] data, int index) {
        if (data.length != 3) {
            throw new IllegalArrayLength(3, data.length);
        }
        try {
            this.set((Col_1) data[0], (Col_2) data[1], (Col_3) data[2], index);
        } catch (ClassCastException err) {
            throw new IllegalArgumentException("Invalid data types provided for columns.", err);
        }
    }
}
