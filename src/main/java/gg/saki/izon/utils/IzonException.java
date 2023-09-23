/*
 * MIT License
 *
 * Copyright (c) 2023 SakiPowered <https://github.com/SakiPowered>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.saki.izon.utils;

import gg.saki.izon.libraries.Library;

import java.util.Objects;

public class IzonException extends RuntimeException {


    private static final long serialVersionUID = -1635129916629589177L;

    private Library relatedLibrary;
    private Library.Status status;

    public IzonException(String message) {
        super(message);
    }

    public IzonException(String message, Throwable cause) {
        super(message, cause);
    }

    public IzonException(Throwable cause) {
        super(cause);
    }

    public IzonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public IzonException(String message, Library relatedLibrary) {
        super(message);
        this.relatedLibrary = relatedLibrary;
    }

    public IzonException(String message, Throwable cause, Library relatedLibrary) {
        super(message, cause);
        this.relatedLibrary = relatedLibrary;
    }

    public IzonException(Throwable cause, Library relatedLibrary) {
        super(cause);
        this.relatedLibrary = relatedLibrary;
    }

    public IzonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Library relatedLibrary, Library.Status status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.relatedLibrary = relatedLibrary;
        this.status = status;
    }

    public IzonException(String message, Library relatedLibrary, Library.Status status) {
        super(message);
        this.relatedLibrary = relatedLibrary;
        this.status = status;
    }

    public IzonException(String message, Throwable cause, Library relatedLibrary, Library.Status status) {
        super(message, cause);
        this.relatedLibrary = relatedLibrary;
        this.status = status;
    }

    public IzonException(Throwable cause, Library relatedLibrary, Library.Status status) {
        super(cause);
        this.relatedLibrary = relatedLibrary;
        this.status = status;
    }

    public boolean hasLibrary() {
        return this.relatedLibrary != null;
    }

    public boolean hasStatus() {
        return this.status != null;
    }

    public Library getRelatedLibrary() {
        return this.relatedLibrary;
    }

    public Library.Status getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;


        IzonException that = (IzonException) o;
        return Objects.equals(relatedLibrary, that.relatedLibrary) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relatedLibrary, status);
    }
}