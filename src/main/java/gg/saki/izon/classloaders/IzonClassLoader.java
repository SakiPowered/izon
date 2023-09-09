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

import gg.saki.izon.classloaders.impl.ReflectionClassLoader;
import gg.saki.izon.classloaders.impl.UnsafeClassLoader;
import gg.saki.izon.utils.IzonException;
import lombok.Getter;
import lombok.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public abstract class IzonClassLoader {

    @Getter
    private final @NonNull URLClassLoader actualLoader;

    public IzonClassLoader(@NonNull URLClassLoader actualLoader) {
        this.actualLoader = actualLoader;
    }



    public abstract void addURL(@NonNull URL url) throws IzonException;

    public void addPath(@NonNull Path path) throws MalformedURLException {
        this.addURL(path.toUri().toURL());
    }

    public static IzonClassLoader create(@NonNull URLClassLoader actualLoader) {
        if (ReflectionClassLoader.isSupported()) {
            return new ReflectionClassLoader(actualLoader);
        }

        if (UnsafeClassLoader.isSupported()) {
            return new UnsafeClassLoader(actualLoader);
        }


        throw new IzonException("Could not find a supported class loader");
    }
}
