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

package gg.saki.izon.classloaders.impl;

import gg.saki.izon.classloaders.IzonClassLoader;
import gg.saki.izon.utils.IzonException;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Objects;

public class UnsafeClassLoader extends IzonClassLoader {

    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;


        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);

            unsafe = (Unsafe) field.get(null);
        } catch (Throwable t) {
            unsafe = null;
        }

        UNSAFE = unsafe;
    }

    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;

    @SuppressWarnings("unchecked")
    public UnsafeClassLoader(@NotNull URLClassLoader actualLoader) {
        super(actualLoader);

        try {
            Object ucp = fetchField(URLClassLoader.class, actualLoader, "ucp");

            this.unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
            this.pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");

        } catch (NoSuchFieldException e) {
            throw new IzonException(e);
        }
    }

    @Override
    public void addURL(@NotNull URL url) throws IzonException {
        if (this.unopenedURLs == null || this.pathURLs == null) {
            throw new IzonException("Could not find unopenedUrls or path fields");
        }

        synchronized (this.unopenedURLs) {
            this.unopenedURLs.add(url);
            this.pathURLs.add(url);
        }
    }

    private Object fetchField(Class<?> clazz, Object object, String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        long offset = UNSAFE.objectFieldOffset(field);

        return UNSAFE.getObject(object, offset);
    }

    public Collection<URL> getUnopenedURLs() {
        return this.unopenedURLs;
    }

    public Collection<URL> getPathURLs() {
        return this.pathURLs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UnsafeClassLoader that = (UnsafeClassLoader) o;
        return Objects.equals(this.unopenedURLs, that.unopenedURLs) && Objects.equals(this.pathURLs, that.pathURLs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.unopenedURLs, this.pathURLs);
    }

    public static boolean isSupported() {
        return UNSAFE != null;
    }
}
