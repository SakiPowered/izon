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

package gg.saki.izon.libraries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class LibraryStatus {

    private final boolean successful;
    private final @NotNull Library relatedLibrary;
    private final @NotNull String message;

    private final @Nullable Throwable failureCause;

    private LibraryStatus(boolean successful, @NotNull Library relatedLibrary, @NotNull String message, @Nullable Throwable failureCause) {
        this.successful = successful;
        this.relatedLibrary = relatedLibrary;
        this.message = message;
        this.failureCause = failureCause;
    }

    public static LibraryStatus success(@NotNull Library relatedLibrary, @NotNull String message) {
        return new LibraryStatus(true, relatedLibrary, message, null);
    }

    public static LibraryStatus failure(@NotNull Library relatedLibrary, @NotNull String message, @Nullable Throwable failureCause) {
        return new LibraryStatus(false, relatedLibrary, message, failureCause);
    }

    public static LibraryStatus failure(@NotNull Library relatedLibrary, @NotNull String message) {
        return new LibraryStatus(false, relatedLibrary, message, null);
    }



    public boolean isSuccessful() {
        return this.successful;
    }

    public @NotNull Library getRelatedLibrary() {
        return this.relatedLibrary;
    }

    public @NotNull String getMessage() {
        return this.message;
    }

    public @NotNull Optional<Throwable> getFailureCause() {
        return Optional.ofNullable(this.failureCause);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryStatus that = (LibraryStatus) o;
        return this.relatedLibrary.equals(that.relatedLibrary) && this.message.equals(that.message) && Objects.equals(this.failureCause, that.failureCause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.relatedLibrary, this.message, this.failureCause);
    }
}