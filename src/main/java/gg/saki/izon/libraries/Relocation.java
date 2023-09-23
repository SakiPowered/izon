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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class Relocation {

    private final @NotNull String pattern;
    private final @NotNull String relocatedPattern;

    private final @Nullable Collection<String> includes;
    private final @Nullable Collection<String> excludes;

    public Relocation(@NotNull String pattern, @NotNull String relocatedPattern, @Nullable Collection<String> includes, @Nullable Collection<String> excludes) {
        this.pattern = pattern.replace("{}", ".");
        this.relocatedPattern = relocatedPattern.replace("{}", ".");
        this.includes = includes;
        this.excludes = excludes;
    }

    public @NotNull String getPattern() {
        return this.pattern;
    }

    public @NotNull String getRelocatedPattern() {
        return this.relocatedPattern;
    }

    public @Nullable Collection<String> getIncludes() {
        return this.includes;
    }

    public @Nullable Collection<String> getExcludes() {
        return this.excludes;
    }

    public boolean hasIncludes() {
        return this.includes != null && !this.includes.isEmpty();
    }

    public boolean hasExcludes() {
        return this.excludes != null && !this.excludes.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relocation that = (Relocation) o;
        return this.pattern.equals(that.pattern) && this.relocatedPattern.equals(that.relocatedPattern) && Objects.equals(this.includes, that.includes) && Objects.equals(this.excludes, that.excludes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.relocatedPattern, this.includes, this.excludes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String pattern;
        private String relocatedPattern;

        private Collection<String> includes;
        private Collection<String> excludes;

        private Builder() {
            // seal class to prevent external instantiation
        }

        public Builder pattern(@NotNull String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder relocatedPattern(@NotNull String relocatedPattern) {
            this.relocatedPattern = relocatedPattern;
            return this;
        }

        public Builder includes(@NotNull Collection<String> includes) {
            this.includes = includes;
            return this;
        }

        public Builder include(@NotNull String include) {
            if (this.includes == null) {
                this.includes = new LinkedList<>();
            }

            this.includes.add(include);
            return this;
        }

        public Builder excludes(@NotNull Collection<String> excludes) {
            this.excludes = excludes;
            return this;
        }

        public Builder exclude(@NotNull String exclude) {
            if (this.excludes == null) {
                this.excludes = new LinkedList<>();
            }

            this.excludes.add(exclude);
            return this;
        }

        public Relocation build() {
            if (this.pattern == null || this.relocatedPattern == null) {
                throw new IllegalStateException("pattern and relocatedPattern cannot be null");
            }

            return new Relocation(this.pattern, this.relocatedPattern, this.includes, this.excludes);
        }
    }
}