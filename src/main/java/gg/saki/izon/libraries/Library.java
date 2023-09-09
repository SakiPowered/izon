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

import java.util.Base64;
import java.util.Collection;
import java.util.LinkedList;

@Data
public class Library {

    private final @NonNull Repository repository;

    private final @NonNull String groupId;
    private final @NonNull String artifactId;
    private final @NonNull String version;

    private final String classifier;

    private final byte[] sha256;

    private final Collection<Relocation> relocations;

    private final @NonNull String path;
    private final String relocatedPath;
    private final @NonNull String friendlyPath;

    public Library(@NonNull Repository repository, @NonNull String groupId, @NonNull String artifactId, @NonNull String version, String classifier, byte[] sha256, Collection<Relocation> relocations) {
        this.repository = repository;

        // Replace all {} with . (to circumvent shading issues)
        this.groupId = groupId.replace("{}", ".");


        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.sha256 = sha256;
        this.relocations = relocations;

        String path = this.groupId.replace('.', '/') + '/' + this.artifactId + '/' + this.version + '/' + this.artifactId + '-' + this.version;
        if (this.hasClassifier()) {
            path += '-' + this.classifier;
        }

        this.path = path + ".jar";
        this.relocatedPath = this.hasRelocations() ? path + "-relocated.jar" : null;

        this.friendlyPath = this.groupId.replace('.', '-') + '-' + this.artifactId + '-' + this.version + (this.hasClassifier() ? '-' + this.classifier : "") + ".jar";

    }

    public boolean hasChecksum() {
        return this.sha256 != null;
    }

    public boolean hasClassifier() {
        return this.classifier != null;
    }

    public boolean hasRelocations() {
        return this.relocations != null && !this.relocations.isEmpty();
    }

    public enum Status {
        SUCCESS, ALREADY_EXISTS, CHECKSUM_MISMATCH, DOWNLOAD_FAILED, RELOCATION_FAILED, LOAD_FAILED;

        public boolean isSuccess() {
            return this == SUCCESS || this == ALREADY_EXISTS;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Repository repository;
        private String groupId;
        private String artifactId;
        private String version;
        private String classifier;
        private byte[] sha256;
        private Collection<Relocation> relocations;

        private Builder() {
            // seal class to prevent external instantiation
        }

        public Builder repository(@NonNull Repository repository) {
            this.repository = repository;
            return this;
        }

        public Builder groupId(@NonNull String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder artifactId(@NonNull String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Builder version(@NonNull String version) {
            this.version = version;
            return this;
        }

        public Builder gav(@NonNull String gav) {
            String[] split = gav.split(":");
            if (split.length != 3) {
                throw new IllegalArgumentException("Invalid GAV: " + gav);
            }

            this.groupId = split[0];
            this.artifactId = split[1];
            this.version = split[2];

            return this;
        }

        public Builder classifier(@NonNull String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Builder checksum(@NonNull byte[] checksum) {
            this.sha256 = checksum;
            return this;
        }

        public Builder checksum(@NonNull String checksum) {
            this.sha256 = Base64.getDecoder().decode(checksum);
            return this;
        }

        public Builder relocations(@NonNull Collection<Relocation> relocations) {
            this.relocations = relocations;
            return this;
        }

        public Builder relocate(@NonNull Relocation relocation) {
            if (this.relocations == null) this.relocations = new LinkedList<>();

            this.relocations.add(relocation);
            return this;
        }

        public Library build() {
            if (this.repository == null || this.groupId == null || this.artifactId == null || this.version == null) {
                throw new IllegalStateException("repository, groupId, artifactId, and version cannot be null");
            }

            return new Library(this.repository, this.groupId, this.artifactId, this.version, this.classifier, this.sha256, this.relocations);
        }
    }
}