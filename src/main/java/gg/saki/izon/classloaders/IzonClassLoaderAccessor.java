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

package gg.saki.izon.classloaders;

import gg.saki.izon.classloaders.impl.ReflectionClassLoaderAccessor;
import gg.saki.izon.classloaders.impl.UnsafeClassLoaderAccessor;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;

public abstract class IzonClassLoaderAccessor {

    private final @NotNull URLClassLoader actualLoader;

    public IzonClassLoaderAccessor(@NotNull URLClassLoader actualLoader) {
        this.actualLoader = actualLoader;
    }

    public abstract void addURL(@NotNull URL url) throws IllegalStateException;

    public void addPath(@NotNull Path path) throws MalformedURLException {
        this.addURL(path.toUri().toURL());
    }

    public @NotNull URLClassLoader getActualLoader() {
        return this.actualLoader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IzonClassLoaderAccessor that = (IzonClassLoaderAccessor) o;
        return this.actualLoader.equals(that.actualLoader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.actualLoader);
    }

    public static IzonClassLoaderAccessor create(@NotNull URLClassLoader actualLoader) {
        if (ReflectionClassLoaderAccessor.isSupported()) {
            return new ReflectionClassLoaderAccessor(actualLoader);
        }

        if (UnsafeClassLoaderAccessor.isSupported()) {
            return new UnsafeClassLoaderAccessor(actualLoader);
        }


        throw new IllegalStateException("Could not find a supported class loader");
    }
}
