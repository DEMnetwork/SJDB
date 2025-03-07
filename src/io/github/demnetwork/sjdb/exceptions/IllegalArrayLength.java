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

package io.github.demnetwork.sjdb.exceptions;

public class IllegalArrayLength extends IllegalArgumentException {
    private final int CL;
    private final int EL;

    public IllegalArrayLength(int length) {
        super("The array has an illegal length(" + length + ")!");
        CL = length;
        EL = -1;
    }

    public IllegalArrayLength() {
        super("The array has an illegal length!");
        CL = -1;
        EL = -1;
    }

    public IllegalArrayLength(Throwable cause) {
        super(cause);
        CL = -1;
        EL = -1;
    }

    public IllegalArrayLength(String message) {
        super(message);
        CL = -1;
        EL = -1;
    }

    public IllegalArrayLength(int ExpectedLength, int CurrentLength) {
        super("The array expects the length " + ExpectedLength + ", but found " + CurrentLength + " as length!");
        this.EL = ExpectedLength;
        this.CL = CurrentLength;
    }

    /**
     * Gets the length that is expected
     * 
     * @return -1 if the expected length is unknown, otherwise it returns the length
     *         that is expected
     */
    public int getExpectedLength() {
        return EL;
    }

    /**
     * Gets the length that was found
     * 
     * @return -1 if the ecurrent length is unknown, otherwise it returns the length
     *         that was found
     */
    public int getCurrentLength() {
        return CL;
    }
}
