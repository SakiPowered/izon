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
import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflectionClassLoader extends IzonClassLoader {

    private static final Method ADD_URL_METHOD;

    static {
        Method addURLMethod;

        try {
            addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
        } catch (Throwable t) {
            addURLMethod = null;
        }

        ADD_URL_METHOD = addURLMethod;
    }

    public ReflectionClassLoader(@NonNull URLClassLoader actualLoader) {
        super(actualLoader);
    }

    @Override
    public void addURL(@NonNull URL url) {
        try {
            ADD_URL_METHOD.invoke(this.getActualLoader(), url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IzonException(e);
        }
    }

    public static boolean isSupported() {
        return ADD_URL_METHOD != null;
    }
}
