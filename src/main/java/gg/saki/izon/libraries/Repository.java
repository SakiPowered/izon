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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class Repository {

    private static Repository MAVEN_CENTRAL;
    private static Repository SAKI_CENTRAL;

    private final @NotNull URL url;
    private final @Nullable String username;
    private final @Nullable String password;

    public Repository(@NotNull URL url, @Nullable String username, @Nullable String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Repository(URL url) {
        this(url, null, null);
    }

    public static Repository getMavenCentral() {
        if (Repository.MAVEN_CENTRAL == null) {
            Repository.MAVEN_CENTRAL = Repository.builder().url("https://repo1.maven.org/maven2/").build();
        }

        return Repository.MAVEN_CENTRAL;
    }

    public static Repository getSakiCentral() {
        if (Repository.SAKI_CENTRAL == null) {
            Repository.SAKI_CENTRAL = Repository.builder().url("https://repo.saki.gg/central/").build();
        }

        return Repository.SAKI_CENTRAL;
    }

    public @NotNull URL getUrl() {
        return this.url;
    }

    public @Nullable String getUsername() {
        return this.username;
    }

    public @Nullable String getPassword() {
        return this.password;
    }

    public boolean hasCredentials() {
        return this.username != null && this.password != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return this.url.equals(that.url) && Objects.equals(this.username, that.username) && Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url, this.username, this.password);
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

        public Builder url(@NotNull URL url) {
            this.url = url;
            return this;
        }

        public Builder url(@NotNull String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }

            return this;
        }

        public Builder username(@NotNull String username) {
            this.username = username;
            return this;
        }

        public Builder password(@NotNull String password) {
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