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

import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.LinkedList;

@Data
public class Relocation {

    private final String pattern;
    private final String relocatedPattern;

    private final Collection<String> includes;
    private final Collection<String> excludes;

    public Relocation(String pattern, String relocatedPattern, Collection<String> includes, Collection<String> excludes) {
        this.pattern = pattern.replace("{}", ".");
        this.relocatedPattern = relocatedPattern.replace("{}", ".");
        this.includes = includes;
        this.excludes = excludes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String pattern;
        private String relocatedPattern;

        private final Collection<String> includes = new LinkedList<>();
        private final Collection<String> excludes = new LinkedList<>();

        private Builder() {
            // seal class to prevent external instantiation
        }

        public Builder pattern(@NonNull String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder relocatedPattern(@NonNull String relocatedPattern) {
            this.relocatedPattern = relocatedPattern;
            return this;
        }

        public Builder include(@NonNull String include) {
            this.includes.add(include);
            return this;
        }

        public Builder exclude(@NonNull String exclude) {
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