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

import gg.saki.izon.utils.IzonException;
import lombok.Data;
import lombok.NonNull;

import java.net.MalformedURLException;
import java.net.URL;

@Data
public class Repository {

    public static final Repository MAVEN_CENTRAL = Repository.builder().url("https://repo1.maven.org/maven2/").build();

    private final URL url;
    private final String username;
    private final String password;

    public Repository(URL url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Repository(URL url) {
        this(url, null, null);
    }

    public boolean hasCredentials() {
        return this.username != null && this.password != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private URL url;
        private String username;
        private String password;

        private Builder() {
            // seal class to prevent external instantiation
        }

        public Builder url(@NonNull URL url) {
            this.url = url;
            return this;
        }

        public Builder url(@NonNull String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new IzonException(e);
            }

            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Repository build() {
            if (this.url == null) {
                throw new IllegalStateException("URL cannot be null");
            }

            return new Repository(this.url, this.username, this.password);
        }
    }
}