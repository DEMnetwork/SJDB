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

package io.github.demnetwork.sjdb.dbelements.property;

/**
 * When this interface is implmented by a class it can have names.
 */
public interface NameProperty {
    /**
     * Used to get the name of the
     * {@link io.github.demnetwork.sjdb.dbelements.DBElement
     * DBElement}
     * 
     * @return The name of the {@link io.github.demnetwork.sjdb.dbelements.DBElement
     *         DBElement}
     */
    public String getName();

    /**
     * Used to set the name of the
     * {@link io.github.demnetwork.sjdb.dbelements.DBElement
     * DBElement}
     * 
     * @param name The new name of the
     *             {@link io.github.demnetwork.sjdb.dbelements.DBElement
     *             DBElement}
     */
    public void setName(String name);
}
